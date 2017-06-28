# CleanArchitecture2

Пример реализации Clean Architecture с использованием EventBus

В основе архитектуры лежит администратор модулей, осуществляющий регистрацию,связывание и отмену регистрации модулей и контроллеров.
Модуль - класс с интерфейсом IModule, являющийся наследником интерфейса ISubscriber. Интерфейс ISubscriber 
имеет единственный метод getName() - получить имя объекта. 
Состав интерфейса IModule:
 - String getSubscriberType() – получить имя типа подписчиков (т.е. тип подписчиков, которыми управляет данный модуль)
 - boolean isPersistent() – флаг, определяющий тип модуля - постоянный/выгружаемый - т.е определяет разрешение операции отмены регистрации
 - void onUnRegister() – событие, которое предваряет операцию отмену регистрации модуля. Используется для внутренних операций в модуле, необходимых  
   перед отменой регистрации

Модули могут иметь подписчиков(слушателей) только одного типа, но быть зависимыми от других модулей. Связывание модулей и взаимную перерегистрацию модулей
осуществляет администратор. Для определения связей модулей и других объектов, класс должен реализовать интерфейс IModuleSubscriber, который является 
наследником интерфейса ISubscriber и имеет единственный метод:
 - List<String> hasSubscriberType() – получить список имен модулей или контроллеров, на которые хочет подписаться объект

Наследником интерфейса IModule является интерфейс ISmallController, имеющий методы:
 - void register(T subscriber) – зарегистрировать подписчика контроллера
 - void unregister(T subscriber) – отписать подписчика контроллера
 - T getSubscriber() – получить первого подписчика. Если подписчики реализуют интерфейс IStateable (имеющий состояние), то выбирается 
   объект, находящий в состоянии STATE_RESUME (текущий на экране). Если нет - выбирается первый по списку.
- Map<String, WeakReference<T>> getSubscribers() - получить список подписчиков контроллера
- boolean hasSubscribers() – признак наличия подписчиков у контроллера

Наследником интерфейса ISmallController является IController:
 - T getCurrentSubscriber() - получить текущего подписчика контроллера
 - void setCurrentSubscriber(T subscriber) – установить текущего подписчика у контроллера

Управлением модулей и контроллеров занимается администратор, реализующий интерфейс IAdmin:
 - <C> C get(String moduleName) – получить модуль/контроллер по его имени
 - void registerModule(IModule module) - зарегистрировать модуль/контроллер. Используется только для Singleton объектов
 - boolean registerModule(String name) – зарегистрировать модуль/контроллер, предварительно создав его. Предпочительный метод.
 - void unregisterModule(String moduleName) - отменить регистрацию модуля/контроллера
 - void register(IModuleSubscriber subscriber) – зарегистрировать подписчика модулей/контроллеров
 - void unregister(IModuleSubscriber subscriber) – отменить регистрацию подписчика
 - void setCurrentSubscriber(IModuleSubscriber subscriber) – сделать подписчика текущим. Администратор сам 
   сделает текущим данного подписчика у всех модулей, на которые подисан объект

В составе данной реализации описаны следующие модули/контроллеры:
 - ApplicationController(Singleton) - служит для получения контекста приложения
 - ErrorController(Singleton) - контроллер ошибок. Предоставляет функционал с регистрацией ошибок в ходе работы приложения
 - EventBusController(Singleton) - контроллер шины событий. Обеспечивает взаимодействие и обмен событиями. Событие - объект, включающий 
   всю информацию для его обработки
 - MemoryCache(Singleton) - кэш в памяти. Очищается при остановке приложения
 - DiskCache(Singleton) - дисковый кэш. Сохраняется при остановке приложения
 - CrashController - контроллер, получающий управление при незапланированных прерываниях приложения
 - ActivityController - контроллер, отвечающий на Activities как View объекты
 - NavigationController - контроллер, отвечающий за жизненный цикл объектов и навигацию между ними
 - PresenterController - контроллер, регистрирующий презентеры и обеспечивающий доступ к ним. Презентеры могут быть локальными,
   т.е. регистрируются только локально или глобальные, которые можно получить через данный контроллер
 - UseCasesController - контроллер, обрабатывающий бизнес и прочую логику приложения
 - MailController - почтовый контроллер. Обеспечивает хранение и доставку почтовых сообщений. Почтовые сообщения хранятся в контроллере и 
   подписчики читают их при переходе в состояние STATE_RESUME. Если получатель сообщения находится в этом же соостянии(STATE_RESUME), 
   то сообщения доставляюся сразу же. Возможно контролирование сообщений на наличие дубликатов, при этом будут удалятся все предыдущие копии данного сообщения.
 - UserIteractionController - отвечает за пользовательсткие действия в приложении. Включает обработку логики, когда пользователь ничего не делает в приложении
   или приложение находится в фоне
 - ContentProvider - объект, отвечающий за взаимодействие с content провайдерами
 - DbProvider - объект, отвечающий за взаимодействие с базами данных. Возможно регистрация нескольких БД
 - NetProvider - объект, отвечающий за взаимодействие с сетью. Поддерживается многонитевое взаимодействие с регулированием
   кол-ва одновременно запущенных запросов в зависимости от типа канала WiFi/LTE/3G/2G, а также остановка выполнения запросов при отсутствии 
   сетевого подключения и ранжирование запросов
 - Repository - объект, осуществляющий выборку данных. Связывает все провайдеры. Поддерживает 8 типов стратегий работы с кэшем 
   (без кеширования, с полным кешированием, чтение только из кэша, чтение без кэша, но запись в кэш и прочее). Данные даоставляются в объект
   назначения через сообщения по шине сообщений(через EventBusController). Предусматривается указание типа источника данных, которое возвращается 
   получателю
 - DesktopController - объект, предоставляющий сервис визуальной настройки рабочего стола пользователя(смену темы и прочее)
 - LocationController - объект, предоставляющий сервис геолокации подписчикам

Для обеспечения фоновых служб, используется класс самовыгружающихся при бездействии сервисов. Релизованы следующие сервисы:
 - сервис вывода сообщений в зону уведомлений
 - сервис сохранения данных в дисковом кэше и кэше памяти

Данная реализация архитектуры, использует методологию - одна активити + множество фрагментов. Activity, предполагается состоящей из Toolbar, Content фрагмента
и бокового меню при горизонтальной ориентации на планшетах. При старте Content фрагмента проводится конфигурация Toolbar и его элементов. Рекомендуется 
использовать следующие раскладки:
 - для смартов с экраном 4/5 дюймов - только вертикальная ориентация (с выпадающем сбоку меню или его отсутствием)
 - для смартов 6 дюймов и планшетов 7/8 дюймов - поддерживать горизонтальную с боковым меню/вертикальную ориентацию
 - для плашетов 9 и выше дюймов - только горизонтальная ориентация с боковым меню

Для взаимодействия с пользователем предусматриваются следущие элементы UI(и сообщения EventBusController):
 - горизонтальный не блокирующий content progress bar
 - круглый блокирующий content progress bar
 - Toolbar progress bar
 - progress bar презенторов
 - Toast сообщения (со сменой цвета и иконок)
 - SnackBar сообщения с обработкой Action
 - автоматическая смена цвета Toolbar при наличие/отсутствии сети
 - поддержка различного класса диалогов






