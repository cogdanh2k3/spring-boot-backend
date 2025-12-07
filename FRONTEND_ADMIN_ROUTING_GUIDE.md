# Hướng dẫn Sửa Frontend để Điều hướng Admin

## ✅ Backend đã OK!

Backend đã trả về đầy đủ thông tin `role` trong API login:

```json
{
  "success": true,
  "message": "Login successful",
  "user": {
    "id": 1,
    "username": "admin",
    "email": "admin@eduquiz.com",
    "fullName": "System Administrator",
    "role": "ADMIN",  // ← Field này đã có
    "phoneNumber": null,
    "profileImageUrl": null
  }
}
```

## 📱 Các bước sửa Frontend Android

### Bước 1: Sửa Data Class - LoginResponse

**File:** `app/src/main/java/com/example/eduquizapp/data/model/LoginResponse.kt`

```kotlin
data class LoginResponse(
    val success: Boolean,
    val message: String?,
    val user: UserData?
)

data class UserData(
    val id: Long,
    val username: String,
    val email: String,
    val fullName: String?,
    val role: String,  // ← Thêm field này
    val phoneNumber: String?,
    val profileImageUrl: String?
)
```

---

### Bước 2: Sửa UserPreferences để lưu Role

**File:** `app/src/main/java/com/example/eduquizapp/data/local/UserPreferences.kt`

```kotlin
class UserPreferences(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(
        "user_prefs", 
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_FULL_NAME = "full_name"
        private const val KEY_ROLE = "role"  // ← Thêm constant này
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
    
    fun saveUserData(
        username: String, 
        email: String, 
        fullName: String? = null,
        role: String = "USER"  // ← Thêm parameter này
    ) {
        sharedPreferences.edit().apply {
            putString(KEY_USERNAME, username)
            putString(KEY_EMAIL, email)
            putString(KEY_FULL_NAME, fullName)
            putString(KEY_ROLE, role)  // ← Lưu role
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }
    
    fun getUserRole(): String {
        return sharedPreferences.getString(KEY_ROLE, "USER") ?: "USER"
    }
    
    fun isAdmin(): Boolean {
        return getUserRole() == "ADMIN"
    }
    
    // ... các method khác giữ nguyên
}
```

---

### Bước 3: Sửa AuthRepository để trả về Role

**File:** `app/src/main/java/com/example/eduquizapp/data/repository/AuthRepository.kt`

```kotlin
class AuthRepository(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {
    
    suspend fun login(usernameOrEmail: String, password: String): Result<String> {
        return try {
            val response = apiService.login(
                LoginRequest(usernameOrEmail, password)
            )
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.user != null) {
                    // Lưu user data kèm role
                    userPreferences.saveUserData(
                        username = body.user.username,
                        email = body.user.email,
                        fullName = body.user.fullName,
                        role = body.user.role  // ← Thêm role
                    )
                    
                    // Trả về role để ViewModel xử lý điều hướng
                    Result.success(body.user.role)
                } else {
                    Result.failure(Exception(body?.message ?: "Login failed"))
                }
            } else {
                Result.failure(Exception("Login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

---

### Bước 4: Sửa LoginViewModel

**File:** `app/src/main/java/com/example/eduquizapp/ui/auth/login/LoginViewModel.kt`

```kotlin
class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()
    
    fun login(usernameOrEmail: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            
            try {
                val result = authRepository.login(usernameOrEmail, password)
                
                if (result.isSuccess) {
                    val role = result.getOrNull() ?: "USER"
                    _loginState.value = LoginState.Success(role)  // ← Trả về role
                } else {
                    _loginState.value = LoginState.Error(
                        result.exceptionOrNull()?.message ?: "Login failed"
                    )
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val role: String) : LoginState()  // ← Thêm role
    data class Error(val message: String) : LoginState()
}
```

---

### Bước 5: Sửa LoginActivity để Điều hướng theo Role

**File:** `app/src/main/java/com/example/eduquizapp/ui/auth/login/LoginActivity.kt`

```kotlin
class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize ViewModel
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        
        setupUI()
        observeLoginState()
    }
    
    private fun setupUI() {
        binding.btnLogin.setOnClickListener {
            val usernameOrEmail = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString()
            
            if (validateInput(usernameOrEmail, password)) {
                loginViewModel.login(usernameOrEmail, password)
            }
        }
    }
    
    private fun observeLoginState() {
        lifecycleScope.launch {
            loginViewModel.loginState.collect { state ->
                when (state) {
                    is LoginState.Loading -> {
                        showLoading()
                    }
                    
                    is LoginState.Success -> {
                        hideLoading()
                        
                        // Điều hướng dựa trên role
                        val intent = if (state.role == "ADMIN") {
                            // Nếu là admin → AdminDashboardActivity
                            Intent(this@LoginActivity, AdminDashboardActivity::class.java)
                        } else {
                            // Nếu là user thường → MainActivity
                            Intent(this@LoginActivity, MainActivity::class.java)
                        }
                        
                        startActivity(intent)
                        finish()
                    }
                    
                    is LoginState.Error -> {
                        hideLoading()
                        showError(state.message)
                    }
                    
                    is LoginState.Idle -> {
                        // Do nothing
                    }
                }
            }
        }
    }
    
    private fun validateInput(usernameOrEmail: String, password: String): Boolean {
        if (usernameOrEmail.isEmpty()) {
            binding.etUsername.error = "Username or email is required"
            return false
        }
        
        if (password.isEmpty()) {
            binding.etPassword.error = "Password is required"
            return false
        }
        
        return true
    }
    
    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false
    }
    
    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnLogin.isEnabled = true
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
```

---

### Bước 6 (Optional): Thêm Check Admin khi App khởi động

**File:** `app/src/main/java/com/example/eduquizapp/ui/splash/SplashActivity.kt`

```kotlin
class SplashActivity : AppCompatActivity() {
    
    private lateinit var userPreferences: UserPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
        userPreferences = UserPreferences(this)
        
        Handler(Looper.getMainLooper()).postDelayed({
            checkLoginStatus()
        }, 2000) // 2 seconds delay
    }
    
    private fun checkLoginStatus() {
        if (userPreferences.isLoggedIn()) {
            // User đã login, check role
            val intent = if (userPreferences.isAdmin()) {
                Intent(this, AdminDashboardActivity::class.java)
            } else {
                Intent(this, MainActivity::class.java)
            }
            startActivity(intent)
        } else {
            // Chưa login → LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}
```

---

## 🧪 Test Flow

1. **Chạy backend:**
   ```bash
   cd spring-boot-backend
   .\mvnw.cmd spring-boot:run
   ```

2. **Test API login:**
   - Mở file `test-admin-login.http`
   - Gửi request và verify response có field `role: "ADMIN"`

3. **Rebuild Android app:**
   ```bash
   ./gradlew clean
   ./gradlew build
   ./gradlew installDebug
   ```

4. **Test login:**
   - Login với username: `admin`, password: `Admin@123`
   - App sẽ tự động chuyển đến `AdminDashboardActivity`
   - Login với user thường → chuyển đến `MainActivity`

---

## 📝 Tóm tắt các file cần sửa trong Frontend

| File | Cần làm gì |
|------|-----------|
| `LoginResponse.kt` | Thêm field `role: String` vào `UserData` |
| `UserPreferences.kt` | Thêm methods `saveUserData(role)`, `getUserRole()`, `isAdmin()` |
| `AuthRepository.kt` | Lưu role khi login, return role từ login method |
| `LoginViewModel.kt` | Thêm role vào `LoginState.Success(role)` |
| `LoginActivity.kt` | Điều hướng dựa vào role: ADMIN → AdminDashboard, USER → MainActivity |
| `SplashActivity.kt` (optional) | Check role khi app khởi động |

---

## ✅ Kết quả mong đợi

- ✅ User `admin` (role = ADMIN) → Vào AdminDashboardActivity
- ✅ User thường (role = USER) → Vào MainActivity
- ✅ Tự động điều hướng đúng khi mở lại app

Chúc bạn thành công! 🎯
