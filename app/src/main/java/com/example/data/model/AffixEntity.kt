package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "affixes")
data class AffixEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "PREFIX" or "SUFFIX"
    val affix: String, // السابقة أو اللاحقة (e.g., un-, -tion)
    val meaning: String, // المعنى أو الوظيفة بالعربية والإنجليزية
    val examples: String, // أمثلة توضيحية
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val TYPE_PREFIX = "PREFIX"
        const val TYPE_SUFFIX = "SUFFIX"

        val DEFAULT_PREFIXES = listOf(
            AffixEntity(type = TYPE_PREFIX, affix = "un-", meaning = "نفي أو عكس المعنى (not / opposite)", examples = "unhappy (حزين), unlock (يفتح), unusual (غير معتاد), unfair (غير عادل)"),
            AffixEntity(type = TYPE_PREFIX, affix = "re-", meaning = "إعادة أو تكرار الفعل (again / back)", examples = "rewrite (يعيد كتابة), redo (يعيد فعل), replay (يعيد تشغيل), review (يراجع)"),
            AffixEntity(type = TYPE_PREFIX, affix = "dis-", meaning = "نفي، إزالة، أو تباعد (not / away / opposite)", examples = "disagree (يعارض), dislike (يكره), disappear (يختفي), discover (يكتشف)"),
            AffixEntity(type = TYPE_PREFIX, affix = "pre-", meaning = "قبل أو مسبقاً (before)", examples = "preview (معاينة), prepay (دفع مسبق), preschool (ما قبل المدرسة), predict (يتنبأ)"),
            AffixEntity(type = TYPE_PREFIX, affix = "mis-", meaning = "بشكل خاطئ أو سيء (wrong / bad)", examples = "mistake (خطأ), misunderstand (يسيء فهم), mislead (يضلل), misuse (يسيء استخدام)"),
            AffixEntity(type = TYPE_PREFIX, affix = "in- / im- / il- / ir-", meaning = "نفي / غير (تتغير حسب الحرف الأول)", examples = "incorrect, impossible, illegal, irregular, immature"),
            AffixEntity(type = TYPE_PREFIX, affix = "over-", meaning = "بإفراط أو زيادة عن الطبيعي (too much / above)", examples = "overcook (يطهو زيادة), overload (حمل زائد), oversleep (يستغرق بالنوم), overdue"),
            AffixEntity(type = TYPE_PREFIX, affix = "under-", meaning = "أقل من اللازم أو تحت (too little / below)", examples = "underestimate (يقلل من شأن), underwater (تحت الماء), underground (تحت الأرض)"),
            AffixEntity(type = TYPE_PREFIX, affix = "sub-", meaning = "فرعي أو تحت (under / secondary)", examples = "submarine (غواصة), subway (مترو أنفاق), substitute (بديل), subtitle (ترجمة نصية)"),
            AffixEntity(type = TYPE_PREFIX, affix = "inter-", meaning = "بيني أو متبادل بين أطراف (between / among)", examples = "international (دولي), interact (يتفاعل), interview (مقابلة), internet (إنترنت)"),
            AffixEntity(type = TYPE_PREFIX, affix = "anti-", meaning = "ضد أو مقاوم لـ (against / opposite)", examples = "antibiotic (مضاد حيوي), antivirus (مضاد فيروسات), antisocial (انطوائي/معادٍ للمجتمع)"),
            AffixEntity(type = TYPE_PREFIX, affix = "non-", meaning = "غير أو لا ينتمي لـ (not / non-existent)", examples = "nonstop (بلا توقف), nonsense (هراء), nonprofit (غير ربحي), nonverbal (غير لفظي)"),
            AffixEntity(type = TYPE_PREFIX, affix = "semi-", meaning = "نصف أو شبه (half / partly)", examples = "semicircle (نصف دائرة), semifinal (نصف النهائي), semifinished (شبه جاهز)"),
            AffixEntity(type = TYPE_PREFIX, affix = "auto-", meaning = "ذاتي أو تلقائي (self / automatic)", examples = "automatic (تلقائي), biography → autobiography (سيرة ذاتية), autopilot (طيار آلي)"),
            AffixEntity(type = TYPE_PREFIX, affix = "bi-", meaning = "ثنائي أو مرتين (two / twice)", examples = "bilingual (ثنائي اللغة), bicycle (دراجة هوائية), biweekly (كل أسبوعين)"),
            AffixEntity(type = TYPE_PREFIX, affix = "multi-", meaning = "متعدد أو كثير (many)", examples = "multilingual (متعدد اللغات), multimedia (وسائط متعددة), multitasking (متعدد المهام)"),
            AffixEntity(type = TYPE_PREFIX, affix = "post-", meaning = "بعد أو لاحق (after / later)", examples = "postgraduate (دراسات عليا), postwar (ما بعد الحرب), postpone (يؤجل)"),
            AffixEntity(type = TYPE_PREFIX, affix = "trans-", meaning = "عبر أو نقل من مكان لآخر (across / through)", examples = "translate (يترجم), transport (ينقل), transform (يتحول), transfer (يحول)"),
            AffixEntity(type = TYPE_PREFIX, affix = "tele-", meaning = "عن بُعد أو عبر مسافة (distant)", examples = "telephone (هاتف), telescope (تلسكوب), television (تلفاز), telework (عمل عن بعد)"),
            AffixEntity(type = TYPE_PREFIX, affix = "en- / em-", meaning = "تمكين أو وضع في حالة (cause to be)", examples = "enable (يمكن), empower (يقوي), enrich (يثري), embrace (يحتضن)"),
            AffixEntity(type = TYPE_PREFIX, affix = "super-", meaning = "فائق أو أعلى من المعتاد (above / beyond)", examples = "superhero (بطل خارق), superstar (نجم ساطع), supermarket (سوبرماركت)"),
            AffixEntity(type = TYPE_PREFIX, affix = "co- / com- / con-", meaning = "معاً أو اشتراك (together / with)", examples = "cooperate (يتعاون), coworker (زميل عمل), connect (يتصل), combine (يدمج)")
        )

        val DEFAULT_SUFFIXES = listOf(
            AffixEntity(type = TYPE_SUFFIX, affix = "-tion / -sion", meaning = "تحويل الفعل إلى اسم حدث أو حالة (noun of action)", examples = "act → action, decide → decision, educate → education, conclude → conclusion"),
            AffixEntity(type = TYPE_SUFFIX, affix = "-able / -ible", meaning = "قابل لـ / يمكن تحقيقه (صفة capability)", examples = "enjoy → enjoyable (ممتع), read → readable (مقروء), flex → flexible (مرن)"),
            AffixEntity(type = TYPE_SUFFIX, affix = "-ful", meaning = "مليء بـ / يتسم بكثرة الصفة (full of)", examples = "hope → hopeful (مفعم بالأمل), beauty → beautiful (جميل), care → careful (حذر)"),
            AffixEntity(type = TYPE_SUFFIX, affix = "-less", meaning = "بدون / فاقد للشيء (without / lacking)", examples = "care → careless (مهمل), hope → hopeless (يائس), home → homeless (مشرد)"),
            AffixEntity(type = TYPE_SUFFIX, affix = "-ness", meaning = "تحويل الصفة إلى اسم حالة (state of being)", examples = "happy → happiness (سعادة), dark → darkness (ظلام), kind → kindness (لطف)"),
            AffixEntity(type = TYPE_SUFFIX, affix = "-ment", meaning = "تحويل الفعل إلى اسم نتيجة أو إجراء (action or result)", examples = "develop → development (تطوير), agree → agreement (اتفاق), move → movement (حركة)"),
            AffixEntity(type = TYPE_SUFFIX, affix = "-ly", meaning = "تحويل الصفة إلى حال/ظرف يبين الكيفية (manner/adverb)", examples = "quick → quickly (بسرعة), slow → slowly (ببطء), easy → easily (بسهولة)"),
            AffixEntity(type = TYPE_SUFFIX, affix = "-er / -or", meaning = "اسم الفاعل للشخص أو الأداة (one who does)", examples = "teach → teacher (معلم), write → writer (كاتب), act → actor (ممثل), visit → visitor"),
            AffixEntity(type = TYPE_SUFFIX, affix = "-ity / -ty", meaning = "تحويل إلى اسم جودة أو خاصية (quality or state)", examples = "active → activity (نشاط), pure → purity (نقاء), safe → safety (أمان)"),
            AffixEntity(type = TYPE_SUFFIX, affix = "-ous / -ious", meaning = "ذو صفة أو متسم بـ (having the quality of)", examples = "danger → dangerous (خطير), fame → famous (مشهور), courage → courageous (شجاع)"),
            AffixEntity(type = TYPE_SUFFIX, affix = "-ize / -ise", meaning = "تحويل الاسم أو الصفة إلى فعل (cause to become)", examples = "modern → modernize (يحدث), memory → memorize (يحفظ), real → realize (يدرك)"),
            AffixEntity(type = TYPE_SUFFIX, affix = "-al", meaning = "متعلق بـ أو ذو طابع (pertaining to)", examples = "nature → natural (طبيعي), music → musical (موسيقي), origin → original (أصلي)"),
            AffixEntity(type = TYPE_SUFFIX, affix = "-ive", meaning = "يتسم بخاصية معينة أو ميال لها (inclined to)", examples = "create → creative (مبدع), act → active (نشط), attract → attractive (جذاب)"),
            AffixEntity(type = TYPE_SUFFIX, affix = "-ship", meaning = "يعبر عن علاقة أو مهارة أو وضع (state or position)", examples = "friend → friendship (صداقة), leader → leadership (قيادة), champion → championship"),
            AffixEntity(type = TYPE_SUFFIX, affix = "-hood", meaning = "مرحلة عمرية أو حالة جماعية (stage or state)", examples = "child → childhood (طفولة), neighbor → neighborhood (حي/مجاورة), brother → brotherhood"),
            AffixEntity(type = TYPE_SUFFIX, affix = "-ish", meaning = "إلى حد ما أو شبيه بـ (somewhat / resembling)", examples = "child → childish (طفولي), red → reddish (مائل للحمرة), fool → foolish (أحمق)"),
            AffixEntity(type = TYPE_SUFFIX, affix = "-ward / -wards", meaning = "الاتجاه أو نحو جهة معينة (direction)", examples = "backward (للخلف), forward (للأمام), upward (للأعلى), homeward (نحو المنزل)"),
            AffixEntity(type = TYPE_SUFFIX, affix = "-age", meaning = "نتيجة أو علاقة أو تجمع (result or collection)", examples = "marry → marriage (زواج), leak → leakage (تسرب), pass → passage (ممر/عبور)"),
            AffixEntity(type = TYPE_SUFFIX, affix = "-ist", meaning = "شخص متخصص أو ممارس لمهنة/عقيدة (specialist)", examples = "art → artist (فنان), piano → pianist (عازف بيانو), science → scientist (عالم)"),
            AffixEntity(type = TYPE_SUFFIX, affix = "-en", meaning = "يجعله كذا أو مصنوع من (made of / make)", examples = "wood → wooden (خشبي), gold → golden (ذهبي), fast → fasten (يثبت/يربط)")
        )
    }
}
