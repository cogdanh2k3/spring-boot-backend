package com.springboot.scene.service;
import com.springboot.scene.entity.SceneLevel;
import com.springboot.scene.entity.SceneLocation;
import com.springboot.scene.repositories.SceneLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;
@Component
public class SceneDataInitializationService implements CommandLineRunner {
    @Autowired
    private SceneLevelRepository levelRepository;
    @Override
    public void run(String... args) throws Exception {
        initializeDefaultLevels();
    }
    private void initializeDefaultLevels() {
// Check if data already exists
        if (levelRepository.count() > 0) {
            System.out.println("Scene levels have been initialized, skipping");
            return;
        }
        System.out.println("Initializing default scene levels...");
// Easy Level (10 locations)
        List<SceneLocation> easyLocations = Arrays.asList(
                new SceneLocation("easy1", "Hoan Kiem Lake", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2FHoanKiemLake_Hanoi.jpg?alt=media&token=bd75cc6d-1276-42b5-9c28-0fe5dde90f42", 21.0285, 105.8542, "Historic lake in the heart of Hanoi", "Hà Nội", "Hoàn Kiếm"),
                new SceneLocation("easy2", "Ho Chi Minh Mausoleum", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2FHoChiMinhMausoleum.jpg?alt=media&token=1c127de4-a2e7-4172-8549-9cb7bc0adbc1", 21.0379, 105.8340, "Mausoleum of President Ho Chi Minh", "Hà Nội", "Ba Đình"),
                new SceneLocation("easy3", "Imperial City of Hue", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2FImperialCityofHue.jpg?alt=media&token=ef562941-2458-48a2-8647-7f4c56959d9d", 16.4690, 107.5792, "UNESCO World Heritage imperial citadel", "Thừa Thiên Huế", "Huế"),
                new SceneLocation("easy4", "Ha Long Bay", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2FHa_Long_Bay.jpg?alt=media&token=d3311920-6fa4-4582-a0b6-39cf598795dc", 20.9101, 107.1839, "Famous bay with limestone karsts", "Quảng Ninh", "Hạ Long"),
                new SceneLocation("easy5", "Phong Nha Cave", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2FPhongnhakebang2.jpg?alt=media&token=d88348b5-8ab5-461f-8532-77d8eafc7848", 17.5827, 106.2867, "World heritage cave in Phong Nha-Kẻ Bàng", "Quảng Bình", "Bố Trạch"),
                new SceneLocation("easy6", "Son Doong Cave", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2FSon_Doong_Cave_5.jpg?alt=media&token=f62955db-918f-4ad2-8ab3-59ecea1becde", 17.4580, 106.2870, "Largest cave in the world", "Quảng Bình", "Bố Trạch"),
                new SceneLocation("easy7", "Golden Bridge", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2FGolden_Bridge_Da-Nang.jpg?alt=media&token=b2e58b33-2fc0-4c60-9cee-2994de31d690", 15.9975, 107.9894, "Iconic hand bridge in Ba Na Hills", "Đà Nẵng", "Hòa Vang"),
                new SceneLocation("easy8", "My Khe Beach", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2Fmykhebeach.jpg?alt=media&token=8557972d-a881-4aa7-a079-2ca91133d537", 16.0595, 108.2471, "Famous beach in Da Nang", "Đà Nẵng", "Sơn Trà"),
                new SceneLocation("easy9", "Hoi An Ancient Town", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2FH%E1%BB%99i_An.jpg?alt=media&token=099717dd-30fb-4668-bb15-8ce8a10fc94a", 15.8801, 108.3380, "UNESCO World Heritage town", "Quảng Nam", "Hội An"),
                new SceneLocation("easy10", "My Son Sanctuary", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2FM%E1%BB%B9_S%C6%A1n.jpg?alt=media&token=e7f02b78-7bf5-4b61-a2e1-073e65691392", 15.7741, 108.1236, "Ancient Cham temple ruins", "Quảng Nam", "Duy Xuyên")
        );
        SceneLevel easyLevel = new SceneLevel("LevelEasy", "Easy Level", "Easy", easyLocations);
// Normal Level (10 locations)
        List<SceneLocation> normalLocations = Arrays.asList(
                new SceneLocation("normal1", "Po Nagar Cham Towers", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2Fpo-nagar-cham-towers.jpg?alt=media&token=d8b81ddb-56c9-4608-ba27-b804b096031b", 12.2570, 109.1940, "Cham temple complex", "Khánh Hòa", "Nha Trang"),
                new SceneLocation("normal2", "Nha Trang Bay", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2FNha-Trang-Bay.jpg?alt=media&token=f7c8c40e-4889-4b92-8955-0852c452f786", 12.2250, 109.2026, "Famous beach bay", "Khánh Hòa", "Nha Trang"),
                new SceneLocation("normal3", "Dalat Xuan Huong Lake", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2FXuan_Huong_Lake_08.jpg?alt=media&token=0a06024c-0fb5-4ee8-8fb2-fb26962d58b1", 11.9436, 108.4424, "Romantic lake in Dalat city center", "Lâm Đồng", "Đà Lạt"),
                new SceneLocation("normal4", "Crazy House", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2Fcrazyhouse.jpg?alt=media&token=10ca1673-96f7-44b2-8811-ba1a94be283a", 11.9404, 108.4323, "Unusual architecture house", "Lâm Đồng", "Đà Lạt"),
                new SceneLocation("normal5", "Fansipan Mountain", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2Ffansipan.jpg?alt=media&token=8f2e055b-3ebb-4ffe-8705-e36590ce8609", 22.3050, 103.7737, "Highest peak in Indochina", "Lào Cai", "Sa Pa"),
                new SceneLocation("normal6", "Bac Ha Market", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2FBac-Ha-Sunday-Market-Featured.jpg?alt=media&token=0633d313-ea97-42ad-b76d-54b1cc4318fc", 22.5472, 104.2860, "Ethnic minority market", "Lào Cai", "Bắc Hà"),
                new SceneLocation("normal7", "Trang An Landscape Complex", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2Ftrangan.jpg?alt=media&token=8f2ccc9a-e21b-4934-a25d-855eec2ee6e6", 20.2525, 105.9230, "UNESCO karst landscape", "Ninh Bình", "Hoa Lư"),
                new SceneLocation("normal8", "Bai Dinh Pagoda", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2FB%C3%81I_%C4%90%C3%8DNH_-_panoramio.jpg?alt=media&token=e4d2bb34-f412-4c2e-bff5-72e29964c6b7", 20.2506, 105.8947, "Largest Buddhist temple in Vietnam", "Ninh Bình", "Gia Viễn"),
                new SceneLocation("normal9", "Tam Coc", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2FTamCoc.jpg?alt=media&token=2400331f-d199-498b-a6cb-c3341d2f00d1", 20.2400, 105.9220, "Boat ride through karst landscape", "Ninh Bình", "Hoa Lư"),
                new SceneLocation("normal10", "Cat Ba Island", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2Fdaocatba.jpg?alt=media&token=000e2e40-c664-4377-8167-e9a49e90911e", 20.8019, 107.0421, "Island with national park", "Hải Phòng", "Cát Hải")
        );
        SceneLevel normalLevel = new SceneLevel("LevelNormal", "Normal Level", "Normal", normalLocations);
// Hard Level (10 locations)
        List<SceneLocation> hardLocations = Arrays.asList(
                new SceneLocation("hard1", "Ban Gioc Waterfall", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2FBangioc.jpg?alt=media&token=000e8826-db8d-4626-ba54-99e1364d8c54", 22.8531, 106.7289, "Border waterfall with China", "Cao Bằng", "Trùng Khánh"),
                new SceneLocation("hard2", "Ba Be Lake", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2Fdu-lich-ho-ba-be-1.jpg?alt=media&token=ce28003f-b455-44f3-a971-2309ed52a98b",  22.4123, 105.6086, "Freshwater lake in Ba Be National Park", "Bắc Kạn", "Ba Bể"),
                new SceneLocation("hard3", "Perfume Pagoda", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2FCh%C3%B9a_H%C6%B0%C6%A1ng.jpg?alt=media&token=b3c01553-6688-47b0-a555-bb76258342be", 20.5778, 105.7306, "Famous Buddhist pilgrimage site", "Hà Nội", "Mỹ Đức"),
                new SceneLocation("hard4", "Thien Mu Pagoda", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2FThienMuTemple.jpg?alt=media&token=81dea9f8-a86d-411a-a999-ffb1db8ba466", 16.4498, 107.5792, "Historic Buddhist pagoda", "Thừa Thiên Huế", "Huế"),
                new SceneLocation("hard5", "Con Dao Islands", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2Fcondao.jpg?alt=media&token=8cfe685d-b5b1-4016-97b7-aff177d3f40a", 8.6866, 106.6090, "Historic island archipelago", "Bà Rịa–Vũng Tàu", "Côn Đảo"),
                new SceneLocation("hard6", "Mui Ne Sand Dunes", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2Fmui-ne.jpg?alt=media&token=a10494dc-a5ff-476d-9950-19b9e4488eb7", 10.9332, 108.2790, "Red and white sand dunes", "Bình Thuận", "Phan Thiết"),
                new SceneLocation("hard7", "Phu Quoc Island", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2Fphuquocjpg.jpg?alt=media&token=f9cbb21d-c71b-4ef7-88df-b8f6d63d0957", 10.2899, 103.9840, "Famous resort island", "Kiên Giang", "Phú Quốc"),
                new SceneLocation("hard8", "Can Tho Floating Market", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2F140344_cantho.jpg?alt=media&token=2b940e44-e507-4fe1-853e-10bd44f1ff1d", 10.0333, 105.7833, "Traditional Mekong Delta floating market", "Cần Thơ", "Cái Răng"),
                new SceneLocation("hard9", "Bitexco Financial Tower", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2FBitexco.jpg?alt=media&token=08c22efd-05b9-4e94-b6bd-c9be44ac4cf9", 10.7719, 106.7049, "Iconic skyscraper", "TP.HCM", "Quận 1"),
                new SceneLocation("hard10", "Cu Chi Tunnels", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/Scene%2FCuChi.JPG?alt=media&token=05b13347-f046-4e14-be3c-50c094243b34", 11.1430, 106.4630, "Historic underground tunnel network", "TP.HCM", "Củ Chi")
        );
        SceneLevel hardLevel = new SceneLevel("LevelHard", "Hard Level", "Hard", hardLocations);
// Save levels
        levelRepository.saveAll(Arrays.asList(easyLevel, normalLevel, hardLevel));
        System.out.println("Successfully initialized " + levelRepository.count() + " scene levels");
    }
}