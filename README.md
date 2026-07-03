# Trendon Store - متجر تريند ون الإلكتروني 🛍️

تطبيق متجر إلكتروني متقدم لـ Android مع تكامل AWS الكامل.

## 💫 الميزات الرئيسية

- ✅ **المصادقة الآمنة** (AWS Cognito) - تسجيل دخول آمن وتحقق من الهوية
- ✅ **إدارة قاعدة البيانات** (AWS DynamoDB) - تخزين منتجات وطلبات وبيانات المستخدمين
- ✅ **تخزين الصور السحابي** (AWS S3) - حفظ صور المنتجات بأمان
- ✅ **نظام الطلبات والدفع** - إدارة الطلبات وطرق الدفع
- ✅ **تتبع الشحنات الفوري** - متابعة حالة الطلب خطوة بخطوة
- ✅ **إدارة الملف الشخصي والعناوين** - حفظ عناوين متعددة
- ✅ **نظام كوبونات الخصم** - تطبيق رموز الخصم
- ✅ **نظام التقييمات والمراجعات** - تقييم المنتجات

## 📋 المتطلبات

```bash
- Android SDK 24+
- Java 11+
- Kotlin 1.8+
- AWS Account
- Android Studio 2022.3+
```

## 🚀 التثبيت السريع

```bash
git clone https://github.com/jehadhasan776-coder/trendon-store.git
cd trendon-store
./gradlew build
```

## 📁 هيكل المشروع

```
trendon-store/
├── aws/
│   ├── cognito/
│   │   └── CognitoAuthManager.kt    # إدارة المصادقة والمستخدمين
│   ├── dynamodb/
│   │   ├── DynamoDBManager.kt       # عمليات CRUD على قاعدة البيانات
│   │   └── Models.kt                # نماذج البيانات
│   └── s3/
│       └── S3Manager.kt             # رفع وتحميل الملفات
├── app/
│   ├── models/                      # نماذج البيانات
│   ├── activities/                  # الشاشات
│   ├── services/                    # الخدمات
│   └── utils/                       # الأدوات المساعدة
├── tests/                           # الاختبارات
├── .github/workflows/               # CI/CD Pipelines
├── build.gradle                     # إعدادات البناء
├── SETUP.md                         # دليل التثبيت المفصل
├── CONTRIBUTING.md                  # إرشادات المساهمة
└── README.md
```

## ⚙️ الإعداد الأولي

### خطوة 1: تثبيت الأدوات المطلوبة

```bash
# تثبيت AWS CLI
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install

# تثبيت AWS Amplify CLI
npm install -g @aws-amplify/cli

# تكوين الوصول إلى AWS
aws configure
```

### خطوة 2: إعداد مشروع Amplify

```bash
amplify init
amplify add auth
amplify add api
amplify add storage
amplify push
```

انظر إلى ملف `SETUP.md` للتعليمات المفصلة.

## 📱 أمثلة الاستخدام

### تسجيل مستخدم جديد

```kotlin
val authManager = CognitoAuthManager(context)

val result = authManager.signUp(
    email = "user@example.com",
    password = "SecurePassword123!",
    phoneNumber = "+962791234567",
    fullName = "أحمد محمد"
)

result.onSuccess { signUpResult ->
    println("تم التسجيل بنجاح: ${signUpResult.userId}")
    // الخطوة التالية: تأكيد البريد الإلكتروني
}.onFailure { error ->
    println("فشل التسجيل: ${error.message}")
}
```

### تسجيل الدخول

```kotlin
val result = authManager.signIn(
    email = "user@example.com",
    password = "SecurePassword123!"
)

result.onSuccess { signInResult ->
    if (signInResult.isSignInComplete) {
        println("تم تسجيل الدخول بنجاح!")
        // انتقل إلى الشاشة الرئيسية
    }
}.onFailure { error ->
    println("فشل تسجيل الدخول: ${error.message}")
}
```

### الحصول على المنتجات

```kotlin
val dbManager = DynamoDBManager(context)

val result = dbManager.getAllProducts()

result.onSuccess { products ->
    products.forEach { product ->
        println("${product.name}: ${product.price} دينار")
    }
}.onFailure { error ->
    println("فشل تحميل المنتجات: ${error.message}")
}
```

### إنشاء طلب جديد

```kotlin
val order = Order(
    userId = "user-123",
    products = listOf(
        OrderItem(
            productId = "prod-456",
            productName = "حذاء رياضي",
            quantity = 1,
            price = 50.0,
            imageUrl = "https://..."
        )
    ),
    totalPrice = 50.0 + 5.0, // السعر + رسوم التوصيل
    status = "معلق",
    shippingAddress = "شارع الملك عبدالله",
    phoneNumber = "+962791234567",
    governorate = "عمّان",
    paymentMethod = "COD", // الدفع عند الاستلام
    shippingCost = 5.0
)

val result = dbManager.createOrder(order)

result.onSuccess { createdOrder ->
    println("تم إنشاء الطلب: ${createdOrder.id}")
}.onFailure { error ->
    println("فشل إنشاء الطلب: ${error.message}")
}
```

### رفع صورة منتج

```kotlin
val s3Manager = S3Manager(context)
val imageFile = File("/path/to/image.jpg")

val result = s3Manager.uploadImage(
    file = imageFile,
    key = "products/trendy-shoe.jpg"
)

result.onSuccess { uploadedKey ->
    // احصل على رابط الصورة
    s3Manager.getImageUrl(uploadedKey).onSuccess { url ->
        println("رابط الصورة: $url")
    }
}.onFailure { error ->
    println("فشل رفع الصورة: ${error.message}")
}
```

### تتبع الطلبات

```kotlin
val result = dbManager.getUserOrders(userId = "user-123")

result.onSuccess { orders ->
    orders.forEach { order ->
        println("الطلب ${order.id}: ${order.status}")
    }
}.onFailure { error ->
    println("فشل تحميل الطلبات: ${error.message}")
}
```

## 🔐 الأمان

- 🔒 **تشفير البيانات**: جميع البيانات مشفرة أثناء النقل
- 🔐 **المصادقة**: عبر AWS Cognito مع كلمات مرور قوية
- 🛡️ **التحكم بالوصول**: عبر AWS IAM Policies
- 🔑 **إدارة المفاتيح**: استخدام AWS KMS

## 🧪 الاختبارات

### تشغيل جميع الاختبارات

```bash
./gradlew test
```

### تشغيل اختبارات محددة

```bash
./gradlew test --tests com.trendonstore.aws.cognito.*
```

### إنشاء تقرير التغطية

```bash
./gradlew testDebugUnitTestCoverage
```

## 🚀 CI/CD

المشروع معد مع GitHub Actions:

### Workflows المتوفرة:

1. **Build & Test** (`.github/workflows/build.yml`)
   - ✅ بناء تلقائي عند كل Push
   - ✅ تشغيل الاختبارات
   - ✅ توليد APK
   - ✅ إنشاء تقارير الأداء

2. **Deploy to AWS** (`.github/workflows/deploy.yml`)
   - ✅ بناء APK للإطلاق
   - ✅ رفع إلى S3
   - ✅ تحديث CloudFront

### إضافة Secrets لـ GitHub:

اذهب إلى: `Settings > Secrets and variables > Actions`

أضف:
```
AWS_ACCESS_KEY_ID = <your-key>
AWS_SECRET_ACCESS_KEY = <your-secret>
AWS_REGION = us-east-1
CLOUDFRONT_DISTRIBUTION_ID = <distribution-id>
```

## 📝 ملاحظات مهمة

### أرقام الهواتف الأردنية

التطبيق معد للأسواق الأردنية:
- **الصيغة**: `+962XXXXXXXXX`
- **المحافظات المدعومة**: جميع محافظات الأردن
- **حساب التوصيل**: يختلف حسب المحافظة

### المحافظات الأردنية المدعومة

```kotlin
val governorates = listOf(
    "عمّان", "الزرقاء", "إربد", "جرش", 
    "عجلون", "المفرق", "البلقاء", "مادبا",
    "الكرك", "الطفيلة", "معان", "العقبة"
)
```

### رسوم التوصيل حسب المحافظة

```kotlin
val SHIPPING_COST_BY_GOVERNORATE = mapOf(
    "عمّان" to 3.0,
    "الزرقاء" to 3.5,
    "إربد" to 5.0,
    // ... والمزيد
)
```

## 🛠️ استكشاف الأخطاء والتحديث

### مشكلة: \"amplifyconfiguration.json not found\"

```bash
# تأكد من وجود الملف في:
app/src/main/res/raw/amplifyconfiguration.json

# إذا لم يكن موجوداً:
amplify push
```

### مشكلة: \"Failed to connect to AWS\"

```bash
# تحقق من بيانات الاعتماد
aws configure

# تأكد من أن المنطقة صحيحة
aws sts get-caller-identity
```

### مشكلة: أخطاء في البناء

```bash
# نظف المشروع
./gradlew clean

# أعد البناء
./gradlew build
```

## 📚 موارد إضافية

- [AWS Amplify Documentation](https://docs.amplify.aws/)
- [AWS Cognito Documentation](https://docs.aws.amazon.com/cognito/)
- [Android Development](https://developer.android.com/)
- [Kotlin Documentation](https://kotlinlang.org/docs/)

## 🤝 المساهمة

نرحب بمساهماتك! انظر إلى ملف `CONTRIBUTING.md` للتفاصيل.

## 📞 التواصل والدعم

- **البريد الإلكتروني**: your-email@example.com
- **GitHub**: [@jehadhasan776-coder](https://github.com/jehadhasan776-coder)
- **Issues**: [أنشئ issue جديد](https://github.com/jehadhasan776-coder/trendon-store/issues/new)

## 📄 الترخيص

هذا المشروع مرخص تحت رخصة MIT - انظر ملف `LICENSE.md` للتفاصيل.

## 🙏 شكر وتقدير

شكراً لاستخدام تريند ون! نأمل أن يساعدك هذا المشروع في بناء متجرك الإلكتروني الخاص.

---

**آخر تحديث**: 2024 ✨
**الحالة**: ✅ جاهز للإنتاج
**الإصدار**: 1.0.0
