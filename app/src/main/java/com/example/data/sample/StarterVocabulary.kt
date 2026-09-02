package com.example.data.sample

import com.example.data.model.Word

object StarterVocabulary {
    fun getStarterWords(): List<Word> {
        val now = System.currentTimeMillis()
        val list = mutableListOf<Word>()

        fun add(
            en: String,
            ar: String,
            cat: String,
            level: String,
            pos: String,
            phonetic: String,
            example: String,
            exampleAr: String,
            status: String = Word.STATUS_NEW,
            isFav: Boolean = false,
            isMast: Boolean = false
        ) {
            list.add(
                Word(
                    id = 0,
                    english = en,
                    arabic = ar,
                    category = cat,
                    level = level,
                    partOfSpeech = pos,
                    pronunciation = phonetic,
                    example = example,
                    exampleArabic = exampleAr,
                    status = status,
                    isFavorite = isFav,
                    isMastered = isMast,
                    createdAt = now,
                    updatedAt = now
                )
            )
        }

        // Daily Life (A1 - B1)
        add("Opportunity", "فرصة", "Business", "B1", "Noun", "/ˌɑːpərˈtuːnəti/", "This job offers a great opportunity for growth.", "توفر هذه الوظيفة فرصة رائعة للنمو.")
        add("Environment", "البيئة", "Science & Nature", "B1", "Noun", "/ɪnˈvaɪrənmənt/", "We must protect the natural environment.", "يجب أن نحمي البيئة الطبيعية.")
        add("Achievement", "إنجاز", "Academic", "B2", "Noun", "/əˈtʃiːvmənt/", "Graduating with honors was her greatest achievement.", "كان التخرج مع مرتبة الشرف أعظم إنجاز لها.")
        add("Accomplish", "ينجز / يحقق", "Daily Life", "B2", "Verb", "/əˈkɑːmplɪʃ/", "You can accomplish anything with hard work.", "يمكنك تحقيق أي شيء بالعمل الجاد.")
        add("Determine", "يحدد / يعزم", "Academic", "B1", "Verb", "/dɪˈtɜːrmɪn/", "Your attitude determines your direction.", "موقفك يحدد اتجاهك.")
        add("Persuade", "يقنع", "Business", "B2", "Verb", "/pərˈsweɪd/", "She managed to persuade the investors.", "تمكنت من إقناع المستثمرين.")
        add("Curious", "فضولي / شغوف بالمعرفة", "Daily Life", "A2", "Adjective", "/ˈkjʊriəs/", "Children are naturally curious about the world.", "الأطفال فضوليون بطبيعتهم حول العالم.")
        add("Courage", "شجاعة", "Daily Life", "B1", "Noun", "/ˈkɜːrɪdʒ/", "It takes courage to stand up for your beliefs.", "يتطلب الأمر شجاعة للدفاع عن معتقداتك.")
        add("Generous", "كريم / سخي", "Daily Life", "A2", "Adjective", "/ˈdʒenərəs/", "He is very generous with his time and resources.", "إنه كريم جداً بوقته وموارده.")
        add("Patience", "صبر", "Daily Life", "B1", "Noun", "/ˈpeɪʃns/", "Learning a language requires time and patience.", "تعلم لغة يتطلب وقتاً وصبراً.")

        // Business & Career (A2 - C1)
        add("Negotiation", "مفاوضة / تفاوض", "Business", "B2", "Noun", "/nɪˌɡoʊʃiˈeɪʃn/", "The contract negotiation went smoothly.", "سارت مفاوضات العقد بسلاسة.")
        add("Strategy", "استراتيجية", "Business", "B1", "Noun", "/ˈstrætədʒi/", "We need a clear strategy to expand our market.", "نحتاج إلى استراتيجية واضحة لتوسيع سوقنا.")
        add("Collaboration", "تعاون / عمل جماعي", "Business", "B2", "Noun", "/kəˌlæbəˈreɪʃn/", "Successful projects rely on close collaboration.", "المشاريع الناجحة تعتمد على التعاون الوثيق.")
        add("Entrepreneur", "رائد أعمال", "Business", "B2", "Noun", "/ˌɑːntrəprəˈnɜːr/", "The young entrepreneur launched a tech startup.", "أطلق رائد الأعمال الشاب شركة تقنية ناشئة.")
        add("Investment", "استثمار", "Business", "B1", "Noun", "/ɪnˈvestmənt/", "Real estate is considered a stable investment.", "يعتبر العقار استثماراً مستقراً.")
        add("Productivity", "إنتاجية", "Business", "B2", "Noun", "/ˌproʊdʌkˈtɪvəti/", "Time management boosts daily productivity.", "إدارة الوقت تعزز الإنتاجية اليومية.")
        add("Revenue", "إيرادات / دخل", "Business", "B2", "Noun", "/ˈrevənuː/", "The company reported a record annual revenue.", "أعلنت الشركة عن إيرادات سنوية قياسية.")
        add("Leadership", "قيادة", "Business", "B1", "Noun", "/ˈliːdərʃɪp/", "Effective leadership inspires the whole team.", "القيادة الفعالة تلهم الفريق بأكمله.")
        add("Feasibility", "جدوى / إمكانية التطبيق", "Business", "C1", "Noun", "/ˌfiːzəˈbɪləti/", "We conducted a feasibility study before launching.", "أجرينا دراسة جدوى قبل الإطلاق.")
        add("Stakeholder", "صاحب مصلحة / شريك معني", "Business", "C1", "Noun", "/ˈsteɪkhoʊldər/", "All key stakeholders agreed on the new policy.", "وافق جميع أصحاب المصلحة الرئيسيين على السياسة الجديدة.")

        // Technology & Computing (A2 - C2)
        add("Algorithm", "خوارزمية", "Technology", "B2", "Noun", "/ˈælɡərɪðəm/", "Search engines use a complex sorting algorithm.", "تستخدم محركات البحث خوارزمية فرز معقدة.")
        add("Innovation", "ابتكار / تجديد", "Technology", "B2", "Noun", "/ˌɪnəˈveɪʃn/", "Innovation drives progress in modern industries.", "الابتكار يقود التقدم في الصناعات الحديثة.")
        add("Artificial Intelligence", "الذكاء الاصطناعي", "Technology", "B1", "Noun", "/ˌɑːrtɪfɪʃl ɪnˈtelɪdʒəns/", "Artificial intelligence is transforming healthcare.", "الذكاء الاصطناعي يغير الرعاية الصحية.")
        add("Cybersecurity", "الأمن السيبراني", "Technology", "B2", "Noun", "/ˈsaɪbərsɪkjʊrəti/", "Companies invest heavily in cybersecurity.", "تستثمر الشركات بكثافة في الأمن السيبراني.")
        add("Automation", "أتمتة / تشغيل آلي", "Technology", "B2", "Noun", "/ˌɔːtəˈmeɪʃn/", "Factory automation increases manufacturing speed.", "أتمتة المصانع تزيد من سرعة التصنيع.")
        add("Cloud Computing", "الحوسبة السحابية", "Technology", "B2", "Noun", "/klaʊd kəmˈpjuːtɪŋ/", "Cloud computing allows remote data access.", "تتيح الحوسبة السحابية الوصول إلى البيانات عن بُعد.")
        add("Scalability", "قابلية التوسع والنمو", "Technology", "C1", "Noun", "/ˌskeɪləˈbɪləti/", "The software architecture is designed for scalability.", "تم تصميم بنية البرنامج لقابلية التوسع.")
        add("Interface", "واجهة مستخدم", "Technology", "B1", "Noun", "/ˈɪntərfeɪs/", "The app has an intuitive user interface.", "يحتوي التطبيق على واجهة مستخدم سهلة الاستخدام.")
        add("Framework", "إطار عمل / هيكل", "Technology", "B2", "Noun", "/ˈfreɪmwɜːrk/", "Jetpack Compose is a modern UI framework.", "جيت باك كومبوز هو إطار عمل حديث للواجهات.")
        add("Repository", "مستودع بيانات / كود", "Technology", "B2", "Noun", "/rɪˈpɑːzətɔːri/", "Code is versioned in a Git repository.", "يتم حفظ إصدارات الكود في مستودع جيت.")

        // Science & Nature (A1 - C1)
        add("Biodiversity", "التنوع البيولوجي", "Science & Nature", "B2", "Noun", "/ˌbaɪoʊdaɪˈvɜːrsəti/", "The rainforest has exceptional biodiversity.", "تحتوي الغابة المطيرة على تنوع بيولوجي استثنائي.")
        add("Ecosystem", "نظام بيئي", "Science & Nature", "B2", "Noun", "/ˈiːkoʊsɪstəm/", "Pollution harms the delicate ocean ecosystem.", "التلوث يضر بالنظام البيئي للمحيطات الهش.")
        add("Sustainability", "استدامة", "Science & Nature", "B2", "Noun", "/səˌsteɪnəˈbɪləti/", "Renewable energy ensures ecological sustainability.", "الطاقة المتجددة تضمن الاستدامة البيئية.")
        add("Atmosphere", "الغلاف الجوي / جو", "Science & Nature", "B1", "Noun", "/ˈætməsfɪr/", "The atmosphere protects Earth from radiation.", "يحمي الغلاف الجوي الأرض من الإشعاع.")
        add("Conservation", "حفظ / حماية الموارد", "Science & Nature", "B2", "Noun", "/ˌkɑːnsərˈveɪʃn/", "Wildlife conservation is crucial for future generations.", "حماية الحياة البرية أمر بالغ الأهمية للأجيال القادمة.")
        add("Renewable", "متجدد", "Science & Nature", "B1", "Adjective", "/rɪˈnuːəbl/", "Solar and wind are renewable energy sources.", "الطاقة الشمسية وطاقة الرياح مصادر متجددة.")
        add("Gravity", "جاذبية", "Science & Nature", "A2", "Noun", "/ˈɡrævəti/", "Gravity keeps planets in orbit around the sun.", "تحافظ الجاذبية على الكواكب في مدارها حول الشمس.")
        add("Evolution", "تطور / ارتقاء", "Science & Nature", "B2", "Noun", "/ˌevəˈluːʃn/", "Scientists study the evolution of species.", "يدرس العلماء تطور الكائنات الحية.")
        add("Precipitation", "هطول الأمطار", "Science & Nature", "C1", "Noun", "/prɪˌsɪpɪˈteɪʃn/", "Heavy precipitation led to regional flooding.", "أدى الهطول الغزير للأمطار إلى فيضانات إقليمية.")
        add("Photosynthesis", "التمثيل الضوئي / البناء الضوئي", "Science & Nature", "B2", "Noun", "/ˌfoʊtoʊˈsɪnθəsɪs/", "Plants convert sunlight through photosynthesis.", "تحول النباتات ضوء الشمس عبر البناء الضوئي.")

        // Health & Psychology (A1 - C1)
        add("Wellbeing", "عافية / راحة نفسية وجسدية", "Health & Mind", "B2", "Noun", "/ˈwelbiːɪŋ/", "Physical activity improves mental wellbeing.", "النشاط البدني يحسن الصحة النفسية والعافية.")
        add("Nutrition", "تغذية", "Health & Mind", "B1", "Noun", "/nuˈtrɪʃn/", "Proper nutrition fuels your body and brain.", "التغذية السليمة تغذي جسمك وعقلك.")
        add("Immunity", "مناعة", "Health & Mind", "B2", "Noun", "/ɪˈmjuːnəti/", "Sleep is essential to build strong immunity.", "النوم ضروري لبناء مناعة قوية.")
        add("Resilience", "مرونة نفسية / قدرة على التعافي", "Health & Mind", "C1", "Noun", "/rɪˈzɪliəns/", "She showed great resilience during difficult times.", "أظهرت مرونة نفسية وقدرة على التحمل في الأوقات الصعبة.")
        add("Mindfulness", "يقظة ذهنية / وعي تام", "Health & Mind", "B2", "Noun", "/ˈmaɪndflnəs/", "Practicing mindfulness reduces daily stress.", "ممارسة اليقظة الذهنية تقلل من التوتر اليومي.")
        add("Endurance", "قوة تحمل", "Health & Mind", "B2", "Noun", "/ɪnˈdʊrəns/", "Marathon runners need incredible endurance.", "يحتاج عداؤو الماراثون إلى قوة تحمل مذهلة.")
        add("Therapy", "علاج / جلسات علاجية", "Health & Mind", "B1", "Noun", "/ˈθerəpi/", "Physical therapy helped him recover quickly.", "ساعده العلاج الطبيعي على التعافي بسرعة.")
        add("Hydration", "ترطيب / شرب السوائل", "Health & Mind", "B1", "Noun", "/haɪˈdreɪʃn/", "Drink water regularly to maintain good hydration.", "اشرب الماء بانتظام للحفاظ على ترطيب جيد.")
        add("Sedentary", "قليل الحركة / خامل", "Health & Mind", "C1", "Adjective", "/ˈsednteri/", "A sedentary lifestyle increases health risks.", "نمط الحياة قليل الحركة يزيد من المخاطر الصحية.")
        add("Cognitive", "إدراكي / معرفي", "Health & Mind", "C1", "Adjective", "/ˈkɑːɡnətɪv/", "Reading enhances cognitive abilities.", "القراءة تعزز القدرات الإدراكية والمعرفية.")

        // Travel & Culture (A1 - B2)
        add("Heritage", "تراث / إرث حضاري", "Travel & Culture", "B2", "Noun", "/ˈherɪtɪdʒ/", "The historic city is a UNESCO World Heritage site.", "المدينة التاريخية موقع تراث عالمي لليونسكو.")
        add("Destination", "وجهة سفر / مقصد", "Travel & Culture", "A2", "Noun", "/ˌdestɪˈneɪʃn/", "Paris is a popular travel destination.", "باريس وجهة سياحية شهيرة.")
        add("Hospitality", "ضيافة / كرم الضيافة", "Travel & Culture", "B2", "Noun", "/ˌhɑːspɪˈtæləti/", "The locals welcomed us with warm hospitality.", "استقبلنا السكان المحليون بحفاوة وكرم ضيافة.")
        add("Itinerary", "جدول الرحلة / خط السير", "Travel & Culture", "B2", "Noun", "/aɪˈtɪnəreri/", "We prepared a detailed travel itinerary.", "أعددنا جدول رحلة مفصل.")
        add("Landscape", "منظر طبيعي", "Travel & Culture", "B1", "Noun", "/ˈlændskeɪp/", "The mountain landscape was breathtaking.", "كان المنظر الطبيعي للجبال خلاباً.")
        add("Architecture", "عمارة / هندسة معمارية", "Travel & Culture", "B1", "Noun", "/ˈɑːrkɪtektʃər/", "The Islamic architecture of Cordoba is stunning.", "العمارة الإسلامية في قرطبة مذهلة.")
        add("Expedition", "رحلة استكشافية", "Travel & Culture", "B2", "Noun", "/ˌekspəˈdɪʃn/", "They embarked on an Arctic expedition.", "انطلقوا في رحلة استكشافية إلى القطب الشمالي.")
        add("Sightseeing", "مشاهدة المعالم السياحية", "Travel & Culture", "A2", "Noun", "/ˈsaɪtsiːɪŋ/", "We spent the afternoon sightseeing in the old town.", "قضينا فترة ما بعد الظهر في زيارة المعالم بالبلدة القديمة.")
        add("Souvenir", "هدية تذكارية", "Travel & Culture", "A2", "Noun", "/ˌsuːvəˈnɪr/", "I bought a handcrafted souvenir for my friend.", "اشتريت هدية تذكارية مصنوعة يدوياً لصديقي.")
        add("Tradition", "تقليد / عادة متوارثة", "Travel & Culture", "A2", "Noun", "/trəˈdɪʃn/", "Celebrating festivals is an important tradition.", "الاحتفال بالمهرجانات تقليد مهم.")

        // Academic & Vocabulary Excellence (B1 - C2)
        add("Comprehend", "يستوعب / يفهم بعمق", "Academic", "B2", "Verb", "/ˌkɑːmprɪˈhend/", "It took time to comprehend the complex theory.", "استغرق الأمر وقتاً لاستيعاب النظرية المعقدة.")
        add("Hypothesis", "فرضية علمية", "Academic", "B2", "Noun", "/haɪˈpɑːθəsɪs/", "The researcher tested her hypothesis thoroughly.", "اختبرت الباحثة فرضيتها بدقة.")
        add("Meticulous", "شديد الدقة / متقن", "Academic", "C1", "Adjective", "/məˈtɪkjələs/", "He is meticulous about citing research sources.", "إنه شديد الدقة في الاستشهاد بمصادر البحث.")
        add("Eloquent", "فصيح / بليغ", "Academic", "C1", "Adjective", "/ˈeləkwənt/", "The speaker gave an eloquent and persuasive lecture.", "ألقى المحاضر كلمة فصيحة ومقنعة.")
        add("Ambiguous", "غامض / يحتمل معنيين", "Academic", "B2", "Adjective", "/æmˈbɪɡjuəs/", "The instructions were ambiguous and caused confusion.", "كانت التعليمات غامضة وتسببت في حدوث ارتباك.")
        add("Coherent", "مترابط / متناسق منطقياً", "Academic", "B2", "Adjective", "/koʊˈhɪrənt/", "Make sure your essay presents a coherent argument.", "تأكد من أن مقالك يقدم حجة متماسكة ومترابطة.")
        add("Elaborate", "يوضح بتفصيل / مفصل", "Academic", "B2", "Verb", "/ɪˈlæbəreɪt/", "Could you elaborate on your main point?", "هل يمكنك الاستفاضة والشرح في نقطتك الرئيسية؟")
        add("Synthesize", "يركّب / يدمج الأفكار", "Academic", "C1", "Verb", "/ˈsɪnθəsaɪz/", "Students learn to synthesize information from sources.", "يتعلم الطلاب دمج وتركيب المعلومات من مصادر متعددة.")
        add("Paradigm", "نموذج فكري / نمط إرشادي", "Academic", "C2", "Noun", "/ˈpærədaɪm/", "Quantum physics caused a paradigm shift in science.", "أحدثت فيزياء الكم تحولاً جذرياً في النموذج العلمي.")
        add("Ubiquitous", "واسع الانتشار / موجود في كل مكان", "Academic", "C2", "Adjective", "/juːˈbɪkwɪtəs/", "Smartphones have become ubiquitous in daily life.", "أصبحت الهواتف الذكية منتشرة في كل مكان في الحياة اليومية.")

        // Idioms & Expressions (B1 - C2)
        add("Piece of cake", "أمر في غاية السهولة", "Idioms & Phrases", "A2", "Idiom", "/piːs əv keɪk/", "The English test was a piece of cake.", "كان اختبار الإنجليزية سهلاً للغاية.")
        add("Break the ice", "يكسر الجليد / يزيل التوتر", "Idioms & Phrases", "B1", "Idiom", "/breɪk ðə aɪs/", "A funny joke helped break the ice at the meeting.", "ساعدت نكتة لطيفة على كسر الجمود في الاجتماع.")
        add("Hit the nail on the head", "أصاب عين الحقيقة", "Idioms & Phrases", "B2", "Idiom", "/hɪt ðə neɪl ɑːn ðə hed/", "Your analysis hit the nail on the head.", "لقد أصاب تحليلك كبد الحقيقة تماماً.")
        add("Once in a blue moon", "نادراً جداً", "Idioms & Phrases", "B1", "Idiom", "/wʌns ɪn ə bluː muːn/", "I eat fast food once in a blue moon.", "أتناول الوجبات السريعة نادراً جداً.")
        add("Burn the midnight oil", "يسهر في العمل أو المذاكرة", "Idioms & Phrases", "B2", "Idiom", "/bɜːrn ðə ˈmɪdnaɪt ɔɪl/", "He burned the midnight oil to prepare for exams.", "سهر الليالي الطوال استعداداً للاختبارات.")
        add("Bite the bullet", "يتحمل أمراً صعباً بشجاعة", "Idioms & Phrases", "B2", "Idiom", "/baɪt ðə ˈbʊlɪt/", "She decided to bite the bullet and apologize.", "قررت أن تتحمل الموقف بشجاعة وتعتذر.")
        add("Through thick and thin", "في السراء والضراء", "Idioms & Phrases", "B2", "Idiom", "/θruː θɪk ænd θɪn/", "True friends stay together through thick and thin.", "الأصدقاء الحقيقيون يبقون معاً في السراء والضراء.")
        add("Call it a day", "ينهي العمل لليوم", "Idioms & Phrases", "A2", "Idiom", "/kɔːl ɪt ə deɪ/", "We worked hard; let us call it a day.", "عملنا بجد؛ دعونا ننهي عمل اليوم ونستريح.")
        add("Cost an arm and a leg", "باهظ الثمن جداً", "Idioms & Phrases", "B1", "Idiom", "/kɔːst ən ɑːrm ænd ə leɡ/", "That luxury watch costs an arm and a leg.", "تلك الساعة الفاخرة باهظة الثمن للغاية.")
        add("Under the weather", "متوعك قليلاً / مريض", "Idioms & Phrases", "A2", "Idiom", "/ˈʌndər ðə ˈweðər/", "I stayed home because I felt under the weather.", "بقيت في المنزل لأنني شعرت بتوعك خفيف.")

        // Fundamental Essentials (A1 - A2)
        add("Essential", "أساسي / جوهري", "Daily Life", "A2", "Adjective", "/ɪˈsenʃl/", "Water is essential for all living creatures.", "الماء ضروري وأساسي لجميع الكائنات الحية.")
        add("Improve", "يحسّن / يطوّر", "Daily Life", "A2", "Verb", "/ɪmˈpruːv/", "Daily reading helps improve your vocabulary.", "القراءة اليومية تساعد على تحسين حصيلتك اللغوية.")
        add("Confidence", "ثقة بالنفس", "Daily Life", "B1", "Noun", "/ˈkɑːnfɪdəns/", "Speaking regularly builds your speaking confidence.", "التحدث بانتظام يبني ثقتك بالنفس.")
        add("Knowledge", "معرفة / علم", "Academic", "A2", "Noun", "/ˈnɑːlɪdʒ/", "Knowledge is the key to unlocking new horizons.", "المعرفة هي المفتاح لفتح آفاق جديدة.")
        add("Fluent", "طليق / فصيح اللسان", "Daily Life", "B1", "Adjective", "/ˈfluːənt/", "She is fluent in three international languages.", "هي طليقة في ثلاث لغات دولية.")
        add("Discover", "يكتشف", "Daily Life", "A2", "Verb", "/dɪˈskʌvər/", "Travel helps you discover new cultures.", "السفر يساعدك على اكتشاف ثقافات جديدة.")
        add("Inspire", "يلهم / يحفز", "Daily Life", "B1", "Verb", "/ɪnˈspaɪər/", "Great teachers inspire students to achieve greatness.", "المعلمون العظماء يلهمون الطلاب لتحقيق التميز.")
        add("Challenge", "تحدٍ / يتحدى", "Daily Life", "A2", "Noun", "/ˈtʃælɪndʒ/", "Overcoming a challenge makes you stronger.", "التغلب على التحدي يجعلك أقوى.")
        add("Dedication", "إخلاص / تفانٍ", "Daily Life", "B2", "Noun", "/ˌdedɪˈkeɪʃn/", "Success requires persistence and dedication.", "النجاح يتطلب المثابرة والتفاني.")
        // Additional Rich Vocabulary across various themes
        add("Magnificent", "رائع / مهيب", "Daily Life", "B1", "Adjective", "/mæɡˈnɪfɪsnt/", "The view from the mountain top was magnificent.", "كان المنظر من قمة الجبل مهيباً ورائعاً.")
        add("Persevere", "يثابر / يواظب", "Academic", "B2", "Verb", "/ˌpɜːrsəˈvɪr/", "If you persevere, you will reach your ultimate goals.", "إذا ثابرت، فستصل إلى أهدافك النهائية.")
        add("Simultaneous", "متزامن / في نفس الوقت", "Technology", "B2", "Adjective", "/ˌsaɪmlˈteɪniəs/", "The app offers simultaneous translations.", "يقدم التطبيق ترجمات متزامنة في نفس الوقت.")
        add("Pragmatic", "عملي / واقعي", "Business", "C1", "Adjective", "/præɡˈmætɪk/", "We need a pragmatic approach to solve this issue.", "نحتاج إلى نهج عملي وواقعي لحل هذه المسألة.")
        add("Spontaneous", "عفوي / تلقائي", "Daily Life", "B2", "Adjective", "/spɑːnˈteɪniəs/", "We made a spontaneous decision to travel.", "اتخذنا قراراً عفوياً بالسفر.")
        add("Authentic", "أصيل / حقيقي", "Travel & Culture", "B2", "Adjective", "/ɔːˈθentɪk/", "We tasted authentic traditional cuisine.", "تذوقنا أطباقاً شعبية تقليدية أصيلة.")
        add("Versatile", "متعدد الاستخدامات / مرن", "Technology", "B2", "Adjective", "/ˈvɜːrsətl/", "Kotlin is a versatile modern programming language.", "كوتلن لغة برمجة حديثة ومتعددة الاستخدامات.")
        add("Pinnacle", "قمة / أوج النجاح", "Academic", "C2", "Noun", "/ˈpɪnəkl/", "Winning the prize was the pinnacle of his career.", "كان الفوز بالجائزة ذروة مسيرته المهنية.")
        add("Ephemeral", "زائل / عابر / قصير الأجل", "Science & Nature", "C2", "Adjective", "/ɪˈfemərəl/", "The morning mist was beautiful but ephemeral.", "كان ضباب الصباح جميلاً لكنه سريع الزوال.")
        add("Conscientious", "مخلص / دقيق الضمير", "Academic", "C1", "Adjective", "/ˌkɑːnʃiˈenʃəs/", "She is a conscientious and diligent researcher.", "هي باحثة مخلصة ودقيقة ومجتهدة.")
        add("Empathy", "تعاطف وجداني / تفهم المشاعر", "Health & Mind", "B2", "Noun", "/ˈempəθi/", "Empathy is essential for building deep human connections.", "التعاطف ضروري لبناء روابط إنسانية عميقة.")
        add("Abundant", "وفير / غزير", "Science & Nature", "B2", "Adjective", "/əˈbʌndənt/", "The region enjoys abundant natural resources.", "تتمتع المنطقة بموارد طبيعية وفيرة.")
        add("Diligent", "مجتهد / مثابر", "Academic", "B1", "Adjective", "/ˈdɪlɪdʒənt/", "Diligent students achieve high test scores.", "الطلاب المجتهدون يحققون درجات اختبار عالية.")
        add("Optimistic", "متفائل", "Daily Life", "A2", "Adjective", "/ˌɑːptɪˈmɪstɪk/", "Always stay optimistic about tomorrow.", "ابق دائماً متفائلاً بشأن الغد.")
        add("Vocabulary", "مفردات / حصيلة لغوية", "Academic", "A2", "Noun", "/vəˈkæbjəleri/", "Expanding your vocabulary opens new worlds.", "توسيع مفرداتك يفتح عوالم جديدة أمامك.")

        return list
    }
}
