# CleanArchitecture2

Пример реализации Clean Architecture с использованием EventBus

В основе архитектуры лежит администратор модулей, осуществляющий регистрацию, связывание и отмену регистрации модулей и контроллеров.
Модуль - класс с интерфейсом IModule, являющийся наследником интерфейса ISubscriber. Интерфейс ISubscriber 
имеет единственный метод getName() - получить имя объекта. 
Состав интерфейса IModule:
 - String getSubscriberType() – получить имя типа подписчиков (т.е. тип подписчиков, которыми управляет данный модуль)
 - boolean isPersistent() – флаг, определяющий тип модуля - постоянный/выгружаемый - т.е определяет разрешение операции отмены регистрации
 - void onUnRegister() – событие, которое предваряет операцию отмену регистрации модуля. Используется для внутренних операций в модуле, необходимых перед отменой регистрации

Модули могут иметь подписчиков(слушателей) только одного типа, но быть зависимыми от других модулей. Связывание модулей и взаимную перерегистрацию модулей
осуществляет администратор. Для определения связей модулей и других объектов, класс должен реализовать интерфейс IModuleSubscriber, который является 
наследником интерфейса ISubscriber и имеет единственный метод:
 - List<String> hasSubscriberType() – получить список имен модулей или контроллеров, на которые хочет подписаться объект

Для обеспечения энергоэффективности предусмотрена реализация самовыгружающихся при бездействии модулей.

Наследником интерфейса IModule является интерфейс ISmallController, имеющий методы:
 - void register(T subscriber) – зарегистрировать подписчика контроллера
 - void unregister(T subscriber) – отписать подписчика контроллера

Наследником интерфейса ISmallController является IController:
 - T getCurrentSubscriber() - получить текущего подписчика контроллера
 - void setCurrentSubscriber(T subscriber) – установить текущего подписчика у контроллера
 - T getSubscriber() – получить первого подписчика. Если подписчики реализуют интерфейс IStateable (имеющий состояние), то выбирается 
   объект, находящий в состоянии STATE_RESUME (текущий на экране). Если нет - выбирается первый по списку.
 - Map<String, WeakReference<T>> getSubscribers() - получить список подписчиков контроллера
 - boolean hasSubscribers() – признак наличия подписчиков у контроллера

Управлением модулей и контроллеров занимается администратор, реализующий интерфейс IAdmin:
 - <C> C get(String moduleName) – получить модуль/контроллер по его имени
 - void registerModule(IModule module) - зарегистрировать модуль/контроллер. Используется только для Singleton объектов
 - boolean registerModule(String name) – зарегистрировать модуль/контроллер, предварительно создав его. Предпочительный метод.
 - void unregisterModule(String moduleName) - отменить регистрацию модуля/контроллера
 - void register(IModuleSubscriber subscriber) – зарегистрировать подписчика модулей/контроллеров
 - void unregister(IModuleSubscriber subscriber) – отменить регистрацию подписчика
 - void setCurrentSubscriber(IModuleSubscriber subscriber) – сделать подписчика текущим. Администратор сам 
   сделает текущим данного подписчика у всех модулей, на которые подписан объект

В составе данной реализации описаны следующие модули/контроллеры:
 - ApplicationController(Singleton) - служит для получения контекста приложения
 - ErrorController(Singleton) - контроллер ошибок. Предоставляет функционал с регистрацией ошибок в ходе работы приложения
 - EventBusController(Singleton) - контроллер шины событий. Обеспечивает взаимодействие и обмен событиями. Событие - объект, включающий 
   всю информацию для его обработки
 - AppPreferencesModule(Singleton) - модeль работы с Application Preferences
 - SerializableMemoryCache(Singleton) - кэш в памяти. Очищается при остановке приложения
 - ParcelableMemoryCache(Singleton) - кэш в памяти. Очищается при остановке приложения
 - SerializableDiskCache(Singleton) - дисковый кэш. Сохраняется при остановке приложения
 - ParcelableDiskCache(Singleton) - дисковый кэш. Сохраняется при остановке приложения
 - CrashController - контроллер, получающий управление при незапланированных прерываниях приложения
 - ActivityController - контроллер, отвечающий за Activities как View объекты
 - NavigationController - контроллер, отвечающий за жизненный цикл объектов и навигацию между ними
 - PresenterController - контроллер, регистрирующий презентеры и обеспечивающий доступ к ним. Презентеры могут быть локальными,
   т.е. регистрируются только локально или глобальные, которые можно получить через данный контроллер
 - UseCasesController - контроллер, обрабатывающий бизнес и прочую логику приложения
 - MailController - почтовый контроллер. Обеспечивает хранение и доставку почтовых сообщений. Почтовые сообщения хранятся в контроллере и 
   подписчики читают их при переходе в состояние STATE_RESUME. Если получатель сообщения находится в этом же соостянии(STATE_RESUME), 
   то сообщения доставляюся сразу же. Возможно контролирование сообщений на наличие дубликатов, при этом будут удалятся все предыдущие копии данного сообщения.
 - UserIteractionController - отвечает за пользовательсткие действия в приложении. Включает обработку логики, когда пользователь ничего не делает в приложении
   или приложение находится в фоне
 - ContentProviderProxy - прокси модуль для ContentProvider
 - ContentProvider - модуль, отвечающий за взаимодействие с content провайдерами
 - DbProvider - модуль, отвечающий за взаимодействие с базами данных. Возможно регистрация нескольких БД
 - NetProvider - модуль, отвечающий за взаимодействие с сетью. Поддерживается многонитевое взаимодействие с регулированием
   кол-ва одновременно запущенных запросов в зависимости от типа канала WiFi/LTE/3G/2G, а также остановка выполнения запросов при отсутствии 
   сетевого подключения и ранжирование запросов
 - Repository - контроллер, осуществляющий выборку данных. Связывает все провайдеры. Поддерживает 8 типов стратегий работы с кэшем 
   (без кеширования, с полным кешированием, чтение только из кэша, чтение без кэша, но запись в кэш и прочее). Данные доставляются в объект
   назначения через сообщения по шине сообщений(через EventBusController). Предусматривается указание типа источника данных, которое возвращается 
   получателю
 - DesktopController - контроллер, предоставляющий сервис визуальной настройки рабочего стола пользователя(смену темы и прочее)
 - LocationController - контроллер, предоставляющий сервис геолокации подписчикам
 - TransformDataModule - модуль, обеспечивающий различные преобразования данных
 - ValidateController - контроллер валидации данных
 - NotificationModule - модуль, обеспечивающий сервис вывода сообщений в различные объекты (зона уведомлений, доска сообщений)

Для обеспечения фоновых служб, используется класс самовыгружающихся при бездействии сервисов. Релизованы следующие сервисы:
 - сервис вывода сообщений в зону уведомлений
 - сервисы сохранения данных в дисковом кэше

Данная реализация архитектуры, использует методологию - одна активити + множество фрагментов. Activity, предполагается состоящей из Toolbar, центральной 
области Content фрагментов и бокового меню при горизонтальной ориентации на планшетах. При старте Content фрагмента проводится конфигурация Toolbar и его элементов. 
Рекомендуется использовать следующие раскладки:
 - для смартов с экраном 4/5 дюймов - только вертикальная ориентация (с выпадающем сбоку меню или его отсутствием)
 - для смартов 6 дюймов и планшетов 7/8 дюймов - поддерживать горизонтальную с боковым меню/вертикальную ориентацию
 - для плашетов 9 и выше дюймов - только горизонтальная ориентация с боковым меню

Для взаимодействия с пользователем использованы следущие элементы UI(и сообщения EventBusController):
 - горизонтальный, не блокирующий содержимого центральной области(content) горизонтальный Toolbar progress bar
 - круглый и блокирующий content progress bar
 - пользовательский Toolbar progress bar
 - секторный и блокирующий content progress bar
 - progress bar презенторов
 - Toast сообщения (со сменой цвета и иконок)
 - SnackBar сообщения с обработкой Action
 - автоматическая смена цвета Toolbar при наличие/отсутствии сети
 - поддержка различного класса диалогов
 - ripple эффект на различных элементах UI
 - поддержка SwipeRefreshLayout
 - Badge на заголовке Toolbar
 - ShortcutBadge в лончерах телефона
 - Tooltip для вывода подсказок
 - Доска сообщений на Expandable Layout

В проект встроен контроллер рабочих столов, реализующий фунционал:
 - настройки тем оформления приложения
 - цветовую гамму приложения и настройку цветов приложения
 - настройку положения элементов приложения

Использованы презентеры:
 - ApplicationSettingsPresenter - предоставляет сервис редактирования настроек приложения
 - ExpandableBoardPresenter - осуществляет контроль доски сообщений
 - OnBackPressedPresenter - предоставляет сервис выхода из приложения по двойному клику на BackPress
 - SettingsDesktopOrderPresenter - управляет настройкой порядка элементов в настройке рабочих столов
 - SideMenuPresenter - предоставляет сервис бокового меню
 - SwipeRefreshPresenter - предоставляет поддержку SwipeRefreshLayout
 - ToolbarPresenter - предоставляет поддежку работы с Toolbar
 - FloatingActionMenuPresenter - пример работы с FabMenu
 - PhoneContactPresenter - пример работы со списком контактов телефона

Архитектура реализует слои:
 - View слой (views и презентеры)
 - слой данных (репозитарий, провайдеры данных и прокси к ним) 
 - слой бизнес логики (usercases)
 - слой сервисов и модулей, обеспечивающий различный функционал

Взаимодействие между слоями (вертикальные связи) осуществляется через шину сообщений. Сообщение - это объект, содержащий все данные для его дальнейшей обработкм.
Горизонтальные связи внутри слоя реализованы через ссылки на объекты (регистрацию модулей и доступ к ним осуществляет администратор).

Поддержку lifecycle activities и fragments осуществляют StateObservable и ViewStateObserver(поддерживающий жизненный цикл активити и фрагмента).
Объекты поддерживают общий интерфейс IStateable. Презентеры используют жизненный цикл view объектов. Для сохранения состояния презенторов предусмотрено 
сохранение текущих состояний презентеров в контроллере презентеров при их разрушении.




