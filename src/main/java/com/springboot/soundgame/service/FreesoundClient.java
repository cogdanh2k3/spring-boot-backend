package com.springboot.soundgame.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FreesoundClient {

    @Value("${freesound.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String BASE_SEARCH_URL = "https://freesound.org/apiv2/search/text/";

    public static class FreesoundResult {
        public long id;
        public String name;
        public String previewUrl;

        public FreesoundResult(long id, String name, String previewUrl) {
            this.id = id;
            this.name = name;
            this.previewUrl = previewUrl;
        }
    }

    public List<FreesoundResult> search(String query, int pageSize) throws Exception {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_SEARCH_URL)
                .queryParam("query", query)
                .queryParam("fields", "id,name,previews")
                .queryParam("page_size", pageSize)
                .queryParam("token", apiKey);

        String url = builder.toUriString();
        ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);

        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Freesound API returned status: " + resp.getStatusCode());
        }

        String body = resp.getBody();
        JsonNode root = objectMapper.readTree(body);
        JsonNode results = root.path("results");
        List<FreesoundResult> list = new ArrayList<>();
        if (results.isArray()) {
            for (JsonNode node : results) {
                long id = node.path("id").asLong();
                String name = node.path("name").asText(null);

                // choose the highest-quality preview available (preview-hq-mp3) fallback to others
                JsonNode previews = node.path("previews");
                String previewUrl = null;
                if (previews.has("preview-hq-mp3")) previewUrl = previews.path("preview-hq-mp3").asText(null);
                if (previewUrl == null && previews.has("preview-lq-mp3")) previewUrl = previews.path("preview-lq-mp3").asText(null);
                if (previewUrl == null && previews.fieldNames().hasNext()) {
                    // pick any preview field if present
                    for (java.util.Iterator<String> it = previews.fieldNames(); it.hasNext(); ) {
                        String key = it.next();
                        previewUrl = previews.path(key).asText(null);
                        if (previewUrl != null) break;
                    }
                }

                if (previewUrl != null) {
                    list.add(new FreesoundResult(id, name, previewUrl));
                }
            }
        }
        return list;
    }

    /**
     * Retrieve detailed sound info by id (if needed).
     * Example endpoint: https://freesound.org/apiv2/sounds/{id}/?token=...
     */
    public FreesoundResult getSoundById(long id) throws Exception {
        String url = "https://freesound.org/apiv2/sounds/" + id + "/?fields=id,name,previews&token=" + apiKey;
        ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Freesound API returned status: " + resp.getStatusCode());
        }
        JsonNode node = objectMapper.readTree(resp.getBody());
        long sid = node.path("id").asLong();
        String name = node.path("name").asText(null);
        JsonNode previews = node.path("previews");
        String previewUrl = null;
        if (previews.has("preview-hq-mp3")) previewUrl = previews.path("preview-hq-mp3").asText(null);
        if (previewUrl == null && previews.has("preview-lq-mp3")) previewUrl = previews.path("preview-lq-mp3").asText(null);
        if (previewUrl == null && previews.fieldNames().hasNext()) {
            for (java.util.Iterator<String> it = previews.fieldNames(); it.hasNext(); ) {
                String key = it.next();
                previewUrl = previews.path(key).asText(null);
                if (previewUrl != null) break;
            }
        }
        if (previewUrl == null) throw new RuntimeException("No preview found for freesound id " + id);
        return new FreesoundResult(sid, name, previewUrl);
    }
}

