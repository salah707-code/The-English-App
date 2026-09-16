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

        // --- Categories (17 Specific Categories) ---
        // 1. آلات
        add("Engine", "محرك", "آلات", "A2", "Noun", "/ˈendʒɪn/", "The car's engine runs quietly.", "محرك السيارة يعمل بهدوء.")
        add("Turbine", "توربين", "آلات", "B2", "Noun", "/ˈtɜːrbaɪn/", "Wind turbines generate clean electricity.", "تولد توربينات الرياح كهرباء نظيفة.")
        add("Crane", "رافعة أثقال", "آلات", "B1", "Noun", "/kreɪn/", "The crane lifted heavy steel beams.", "رفعت الرافعة عوارض فولاذية ثقيلة.")
        add("Generator", "مولد كهربائي", "آلات", "B1", "Noun", "/ˈdʒenəreɪtər/", "A backup generator supplied power.", "زود المولد الاحتياطي الطاقة أثناء الانقطاع.")
        add("Drill", "مثقاب / دريل", "آلات", "A2", "Noun", "/drɪl/", "He used an electric drill to make a hole.", "استخدم مثقاباً كهربائياً لعمل ثقب.")

        // 2. أبعاد و قياسات و أحجام
        add("Dimension", "بُعد / قياس", "أبعاد و قياسات و أحجام", "B1", "Noun", "/daɪˈmenʃn/", "The room has large dimensions.", "الغرفة ذات أبعاد واسعة.")
        add("Diameter", "قطر الدائرة", "أبعاد و قياسات و أحجام", "B2", "Noun", "/daɪˈæmɪtər/", "Measure the circle's diameter carefully.", "قس قطر الدائرة بعناية.")
        add("Altitude", "ارتفاع عن سطح البحر", "أبعاد و قياسات و أحجام", "B2", "Noun", "/ˈæltɪtuːd/", "The airplane reached cruising altitude.", "وصلت الطائرة إلى ارتفاع التحليق.")
        add("Volume", "حجم / سعة", "أبعاد و قياسات و أحجام", "B1", "Noun", "/ˈvɑːljuːm/", "Calculate the container's total volume.", "احسب الحجم الكلي للحاوية.")
        add("Perimeter", "محيط الشكل", "أبعاد و قياسات و أحجام", "B2", "Noun", "/pəˈrɪmɪtər/", "Security fences surround the perimeter.", "أسوار أمنية تحيط بالمحيط الخارجي.")

        // 3. أديان
        add("Faith", "إيمان / عقيدة", "أديان", "B1", "Noun", "/feɪθ/", "Faith gives people inner peace and strength.", "الإيمان يمنح الناس السلام الداخلي والقوة.")
        add("Worship", "عبادة / يتعبد", "أديان", "B1", "Noun", "/ˈwɜːrʃɪp/", "Places of worship are sacred for communities.", "دور العبادة مقدسة بالنسبة للمجتمعات.")
        add("Pilgrimage", "حج / رحلة مقدسة", "أديان", "B2", "Noun", "/ˈpɪlɡrɪmɪdʒ/", "Millions perform the annual pilgrimage.", "الملايين يؤدون فريضة الحج السنوية.")
        add("Sacred", "مقدس", "أديان", "B2", "Adjective", "/ˈseɪkrɪd/", "The ancient shrine is considered sacred.", "يعتبر المزار القديم مقدساً.")

        // 4. أفعال إيجابية
        add("Encourage", "يشجع / يحفز", "أفعال إيجابية", "A2", "Verb", "/ɪnˈkɜːrɪdʒ/", "Teachers encourage students to read more.", "يشجع المعلمون الطلاب على القراءة أكثر.")
        add("Flourish", "يزدهر / ينمو بنجاح", "أفعال إيجابية", "B2", "Verb", "/ˈflɜːrɪʃ/", "The economy began to flourish.", "بدأ الاقتصاد في الازدهار والنمو.")
        add("Empower", "يمكّن / يقوي", "أفعال إيجابية", "B2", "Verb", "/ɪmˈpaʊər/", "Education empowers youth to lead.", "التعليم يمكّن الشباب من القيادة.")
        add("Reconcile", "يصلح / يوفق بين", "أفعال إيجابية", "C1", "Verb", "/ˈrekənsaɪl/", "They managed to reconcile their differences.", "تمكنوا من التوفيق بين خلافاتهم والصلح.")

        // 5. أفعال سلبية
        add("Betray", "يخون / يغدر", "أفعال سلبية", "B2", "Verb", "/bɪˈtreɪ/", "He would never betray a loyal friend.", "لن يخون أبداً صديقاً مخلصاً.")
        add("Deteriorate", "يتدهور / يسوء", "أفعال سلبية", "B2", "Verb", "/dɪˈtɪriəreɪt/", "Weather conditions began to deteriorate.", "بدأت الأحوال الجوية في التدهور.")
        add("Neglect", "يهمل / يتجاهل", "أفعال سلبية", "B2", "Verb", "/nɪˈɡlekt/", "Do not neglect your health and rest.", "لا تهمل صحتك وقسطك من الراحة.")
        add("Deceive", "يخدع / يضلل", "أفعال سلبية", "B2", "Verb", "/dɪˈsiːv/", "Appearances can often deceive you.", "المظاهر كثيراً ما تخدع الإنسان.")

        // 6. إدارة و سياسة و قانون
        add("Legislation", "تشريع / قوانين مسنونة", "إدارة و سياسة و قانون", "B2", "Noun", "/ˌledʒɪsˈleɪʃn/", "Parliament approved the new legislation.", "وافق البرلمان على التشريع الجديد.")
        add("Governance", "حوكمة / إدارة رشيدة", "إدارة و سياسة و قانون", "C1", "Noun", "/ˈɡʌvərnəns/", "Good governance ensures fair development.", "الحوكمة الرشيدة تضمن تنمية عادلة.")
        add("Jurisdiction", "ولاية قضائية / اختصاص قانوني", "إدارة و سياسة و قانون", "C1", "Noun", "/ˌdʒʊrɪsˈdɪkʃn/", "The court has jurisdiction over this case.", "للمحكمة اختصاص قضائي في هذه القضية.")
        add("Decree", "مرسوم / قرار رسمي", "إدارة و سياسة و قانون", "C1", "Noun", "/dɪˈkriː/", "The minister issued an official decree.", "أصدر الوزير مرسوماً رسمياً.")

        // 7. بصر و رؤية
        add("Glimpse", "لمحة / نظرة خاطفة", "بصر و رؤية", "B1", "Noun", "/ɡlɪmps/", "I caught a glimpse of the shooting star.", "ألقيت نظرة خاطفة على الشهاب العابر.")
        add("Perceive", "يدرك بالبصر / يلاحظ", "بصر و رؤية", "B2", "Verb", "/pərˈsiːv/", "I could perceive a faint light ahead.", "استطعت إدراك ضوء خافت في الأمام.")
        add("Illuminate", "يضيء / ينير", "بصر و رؤية", "B2", "Verb", "/ɪˈluːmɪneɪt/", "Lanterns illuminate the garden walkway.", "الفوانيس تنير الممر في الحديقة.")
        add("Perspective", "منظور / زاوية رؤية", "بصر و رؤية", "B2", "Noun", "/pərˈspektɪv/", "Look at the problem from another perspective.", "انظر إلى المشكلة من منظور وزاوية أخرى.")

        // 8. حركات
        add("Accelerate", "يتسارع / يزيد السرعة", "حركات", "B2", "Verb", "/əkˈseləreɪt/", "The vehicle began to accelerate rapidly.", "بدأت المركبة تتسارع بسرعة كبيرة.")
        add("Glide", "ينزلق / يحلق بسلاسة", "حركات", "B1", "Verb", "/ɡlaɪd/", "The eagle glides effortlessly across the sky.", "يحلق النسر بانسيابية في كبد السماء.")
        add("Propel", "يدفع للأمام / يسير", "حركات", "B2", "Verb", "/prəˈpel/", "Jets propel the aircraft into flight.", "المحركات النفاثة تدفع الطائرة للطيران.")
        add("Oscillate", "يتذبذب / يتأرجح", "حركات", "C1", "Verb", "/ˈɑːsɪleɪt/", "The pendulum oscillates back and forth.", "يتأرجح البندول ذهاباً وإياباً.")

        // 9. صفات جيدة
        add("Benevolent", "خيّر / محب للخير", "صفات جيدة", "C1", "Adjective", "/bəˈnevələnt/", "The benevolent leader helped those in need.", "ساعد القائد الخيّر المحتاجين.")
        add("Trustworthy", "جدير بالثقة / أمين", "صفات جيدة", "B1", "Adjective", "/ˈtrʌstwɜːrði/", "He is a reliable and trustworthy partner.", "إنه شريك موثوق وجدير بالأمانة.")
        add("Sincere", "مخلص / صادق النية", "صفات جيدة", "B1", "Adjective", "/sɪnˈsɪr/", "Accept my sincere gratitude.", "تقبل خالص امتناني وتقديري الصادق.")
        add("Courageous", "شجاع / مقدام", "صفات جيدة", "B1", "Adjective", "/kəˈreɪdʒəs/", "The firefighter made a courageous rescue.", "قام رجل الإطفاء بعملية إنقاذ شجاعة.")

        // 10. صفات سلبية
        add("Arrogant", "متكبر / مغرور", "صفات سلبية", "B1", "Adjective", "/ˈærəɡənt/", "His arrogant behavior alienated his friends.", "سلوكه المتكبر أبعد أصدقاءه عنه.")
        add("Hostile", "عدائي / غير ودي", "صفات سلبية", "B2", "Adjective", "/ˈhɑːstl/", "They faced a hostile reception.", "واجهوا استقبالاً عدائياً.")
        add("Reckless", "متهور / طائش", "صفات سلبية", "B2", "Adjective", "/ˈrekləs/", "Reckless driving causes serious accidents.", "القيادة المتهورة تسبب حوادث خطيرة.")
        add("Cynical", "ساخر بسوداوية / متشائم", "صفات سلبية", "B2", "Adjective", "/ˈsɪnɪkl/", "Do not become cynical about honesty.", "لا تكن ساخراً أو فاقد الثقة في الصدق.")

        // 11. طبيعة
        add("Glacier", "نهر جليدي", "طبيعة", "B1", "Noun", "/ˈɡleɪʃər/", "The ancient glacier is melting slowly.", "النهر الجليدي القديم يذوب ببطء.")
        add("Meadow", "مرج أخضر / روضة", "طبيعة", "B2", "Noun", "/ˈmedoʊ/", "Wildflowers bloomed in the sunny meadow.", "أزهرت الزهور البرية في المرج المشمس.")
        add("Horizon", "أفق", "طبيعة", "B1", "Noun", "/həˈraɪzn/", "The sun disappeared below the horizon.", "غابت الشمس تحت خط الأفق.")
        add("Canopy", "مظلة الغابة الشجرية", "طبيعة", "B2", "Noun", "/ˈkænəpi/", "Birds nest high in the forest canopy.", "تعشش الطيور عالياً في مظلة الغابة الشجرية.")

        // 12. علوم
        add("Molecule", "جزيء", "علوم", "B2", "Noun", "/ˈmɑːlɪkjuːl/", "Water consists of hydrogen and oxygen molecules.", "يتكون الماء من جزيئات الهيدروجين والأكسجين.")
        add("Synthesis", "تخليق / توليف كيميائي", "علوم", "C1", "Noun", "/ˈsɪnθəsɪs/", "The chemical synthesis yielded a new compound.", "أسفر التخليق الكيميائي عن مركب جديد.")
        add("Genetics", "علم الوراثة", "علوم", "B2", "Noun", "/dʒəˈnetɪks/", "Modern medicine relies heavily on genetics.", "يعتمد الطب الحديث بشكل كبير على علم الوراثة.")
        add("Quantum", "كمومي / كوانتم", "علوم", "C1", "Adjective", "/ˈkwɑːntəm/", "Quantum computing will revolutionize speed.", "الحوسبة الكمومية ستحدث ثورة في سرعة المعالجة.")

        // 13. فترة زمنية
        add("Era", "عصر / حقبة تاريخية", "فترة زمنية", "B1", "Noun", "/ˈɪrə/", "The digital era transformed human life.", "غيّر العصر الرقمي حياة البشرية.")
        add("Epoch", "فترة زمنية بارزة", "فترة زمنية", "C1", "Noun", "/ˈepək/", "The discovery marked a new epoch in science.", "شكل هذا الاكتشاف حقبة جديدة في تاريخ العلم.")
        add("Twilight", "شفق / غسق", "فترة زمنية", "B2", "Noun", "/ˈtwaɪlaɪt/", "Stars begin to appear at twilight.", "تبدأ النجوم بالظهور عند الشفق المسائي.")
        add("Century", "قرن من الزمان / مئة عام", "فترة زمنية", "A2", "Noun", "/ˈsentʃəri/", "The castle was built in the sixteenth century.", "شُيدت القلعة في القرن السادس عشر.")

        // 14. فنون و آداب
        add("Masterpiece", "تحفة فنية / عمل رائع", "فنون و آداب", "B2", "Noun", "/ˈmæstərpiːs/", "The Mona Lisa is a timeless masterpiece.", "تعتبر الموناليزا تحفة فنية خالدة.")
        add("Metaphor", "استعارة مجازية", "فنون و آداب", "B2", "Noun", "/ˈmetəfɔːr/", "Poets often use metaphor to convey emotions.", "يستخدم الشعراء الاستعارة للتعبير عن المشاعر.")
        add("Calligraphy", "فن الخط العربي", "فنون و آداب", "B2", "Noun", "/kəˈlɪɡrəfi/", "Arabic calligraphy is a celebrated art form.", "الخط العربي فن عريق ومشهود له بالإبداع.")
        add("Prose", "نثر أدبي", "فنون و آداب", "B2", "Noun", "/proʊz/", "Her prose is rich and vivid.", "نثرها الأدبي غني ومليء بالحيوية.")

        // 15. مباني
        add("Skyscraper", "ناطحة سحاب", "مباني", "A2", "Noun", "/ˈskaɪskreɪpər/", "The skyscraper towers over the city center.", "ترتفع ناطحة السحاب فوق مركز المدينة.")
        add("Fortress", "حصن / قلعة منيعة", "مباني", "B2", "Noun", "/ˈfɔːrtrəs/", "The ancient stone fortress stood undefeated.", "وقف الحصن الحجري صامداً عبر القرون.")
        add("Monument", "نصب تذكاري / معلم أثري", "مباني", "B1", "Noun", "/ˈmɑːnjumənt/", "Tourists gathered near the national monument.", "تجمع السياح بالقرب من النصب التذكاري الوطني.")
        add("Cathedral", "كاتدرائية / صرح تاريخي", "مباني", "B1", "Noun", "/kəˈθiːdrəl/", "The cathedral features grand gothic arches.", "تتميز الكاتدرائية بأقواس معمارية مهيبة.")

        // 16. مشاعر و عواطف
        add("Compassion", "عطف / رحمة وشفقة", "مشاعر و عواطف", "B2", "Noun", "/kəmˈpæʃn/", "Treat all creatures with kindness and compassion.", "عامل جميع الكائنات بلطف ورأفة ورحمة.")
        add("Gratitude", "امتنان / عرفان بالجميل", "مشاعر و عواطف", "B1", "Noun", "/ˈɡrætɪtuːd/", "Expressing gratitude brightens your spirit.", "التعبير عن الامتنان والحمد يبهج الروح.")
        add("Euphoria", "نشوة / غمرة فرح", "مشاعر و عواطف", "C1", "Noun", "/juːˈfɔːriə/", "The team was in euphoria after winning.", "غمرت الفريق نشوة وفرحة غامرة بعد الفوز.")
        add("Nostalgia", "حنين إلى الماضي / نوستالجيا", "مشاعر و عواطف", "B2", "Noun", "/nɑːˈstældʒə/", "Old photographs filled him with nostalgia.", "ملأت الصور القديمة قلبه بالحنين إلى الماضي.")

        // 17. موسيقى و أصوات
        add("Melody", "لحن موسيقي", "موسيقى و أصوات", "A2", "Noun", "/ˈmelədi/", "The catchy melody stayed in my head all day.", "بقي اللحن الموسيقي العذب في رأسي طوال اليوم.")
        add("Harmony", "هارموني / انسجام نغمي", "موسيقى و أصوات", "B2", "Noun", "/ˈhɑːrməni/", "The choir sang in perfect harmony.", "غنت فرقة الكورال بانسجام وتناغم تام.")
        add("Resonance", "رنين / صدى صوتي", "موسيقى و أصوات", "B2", "Noun", "/ˈrezənəns/", "The acoustic guitar produces deep resonance.", "ينتج الجيتار الصوتي رنيناً عميقاً وغنياً.")
        add("Acoustics", "علم الصوتيات / جودة الصوت", "موسيقى و أصوات", "B2", "Noun", "/əˈkuːstɪks/", "The hall has exceptional acoustics for concerts.", "تتمتع القاعة بجودة صوتيات استثنائية للحفلات.")

        // 18. العبارات الشائعة
        add("Piece of cake", "أمر في غاية السهولة / سهل جداً", "العبارات الشائعة", "A2", "Phrase", "/piːs əv keɪk/", "Don't worry about the quiz, it's a piece of cake!", "لا تقلق بشأن الاختبار، فهو سهل جداً وفي غاية البساطة!")
        add("Break a leg", "أتمنى لك التوفيق / حظاً سعيداً", "العبارات الشائعة", "B1", "Phrase", "/breɪk ə leɡ/", "You will do great in your speech today, break a leg!", "ستبلي بلاءً حسناً في إلقائك اليوم، أتمنى لك التوفيق!")
        add("Once in a blue moon", "نادراً جداً / في مناسبات متباعدة", "العبارات الشائعة", "B2", "Phrase", "/wʌns ɪn ə bluː muːn/", "He lives far away, so we only meet once in a blue moon.", "إنه يسكن بعيداً، لذا فإننا نلتقي نادراً جداً.")
        add("Better late than never", "أن تأتي متأخراً خير من ألا تأتي أبداً", "العبارات الشائعة", "A2", "Phrase", "/ˈbetər leɪt ðæn ˈnevər/", "She finally arrived at the meeting; better late than never.", "وصلت أخيراً إلى الاجتماع، أن تأتي متأخراً خير من ألا تأتي أبداً.")
        add("Actions speak louder than words", "الأفعال أبلغ وأصدق من الأقوال", "العبارات الشائعة", "B1", "Phrase", "/ˈækʃənz spiːk ˈlaʊdər/", "He promised to help, but actions speak louder than words.", "وعد بأن يقدم يد العون، لكن الأفعال دائماً أصدق من الأقوال.")
        add("Call it a day", "يكتفي بهذا القدر / ينهي العمل لهذا اليوم", "العبارات الشائعة", "B1", "Phrase", "/kɔːl ɪt ə deɪ/", "We've made great progress, let's call it a day.", "أحرزنا تقدماً رائعاً، فلنكتفِ بهذا القدر لليوم.")
        add("Hit the books", "يبدأ بالمذاكرة بجد واجتهاد", "العبارات الشائعة", "B1", "Phrase", "/hɪt ðə bʊks/", "Exam season is approaching, time to hit the books!", "اقترب موسم الامتحانات، حان وقت الجد والمذاكرة بتركيز!")
        add("Under the weather", "يشعر بتوعك أو إرهاق خفيف", "العبارات الشائعة", "B1", "Phrase", "/ˈʌndər ðə ˈweðər/", "I felt a bit under the weather so I rested at home.", "شعرت بوعكة صحية طفيفة ففضلت الاستراحة في المنزل.")

        return list
    }
}
