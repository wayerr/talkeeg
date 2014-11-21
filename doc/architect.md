# Проект takeeg #

## Архитектура ##

##Термины##

* user - пользователь, может владеть несколькими client, но также несколько client могут принадлежать одному user (предположим что пользователь обладает master ключем, это облегчает обмен ключами между стройствами, т.к. клиент просто подписывает все публичные ключи своих устройств)
* user public key (UPubK) - ключ для проверки подписи
* user id (UID) - отпечаток UPubK, 128bit
* user private Key (UPrK)
* user identity card (UIC) - публичный ключ клиента, и его идентификационная информация
* client - экземпляр ПО на устройстве пользователя выполняющий роль клиента
* server - экземпляр ПО на устройстве имеющем бОльший аптайм и выполняющий роль "посредника" между клиентами
* client private key (CPrK) - приватный ключ клиента (в случае сниженной безопасности может быть эквивалентен)
* client public key (CPubK)- публичный ключ клиента
* client id (CID) - отпечаток публичного ключа клиента 128bit (возможно CID будет ipv6 адресом см. http://en.wikipedia.org/wiki/Cryptographically_Generated_Address )
* client identity card (СIC) - публичный ключ и совокупность информации о клиенте подписанное его приватным ключем (ЭЦП)
* acquainted user (AU) - пользователь identity card которого сохранена на текущем клиенте (очевидно, клиенты одного пользователя должны этой информацией обмениваться)
* acquainted client (AC) - клиент identity card которого сохранена на текущем клиенте
* clients acquaintance (CA) - процесс обмена клиентов их identity card
* transport - способ коммуникации client-client и client-server, по умолчанию поддерживается udp (возможно rudp) транспорт поддерживающий ipv4/ipv6 адреса (возможно будет полезно иметь транспорт через bluetooth, анонимных децентрализованных сетей и т.п.)

## Требования ##

* публикация на server UIC
* публикация на server информации о всех устройствах текущего user
* публикация на server информации о всех адресах текущего client (локальные и внешние ipv4, ipv6, доменное имя, возможно адреса bluetooth и анонимных децентрализованных сетей)
* сохранение на server списка acquainted users (не acquainted user не может получить информацию о адресах клиента)
* user выбирает root client на котором хранится UPrK и выполняет CA для каждого устройства (все клиенты к ), затем выдает сертификаты для каждого client
* clients acquaintance: достоверная передача identity card с одного client на другой не допуская возможных MitM атак 
    * по NFC
    * упаковка identity card в qr-code (надо определить размер, возможно поместится только списко адресов и отпечаток публичного ключа)
    * вручную (через файл или e-mail) c обязательной возможностью проверить client public key fingeprint
    * через поиск на сервере по неким критериям (так же с проверкой fingeprint)
    * между устроствами одного пользователя, автоматически через одну локальную сеть, qr-code, BT, или server, ключи _должны_ быть подписаны пользователем
* передача произвольных сообщений (в т.ч. произвольного размера) между различными client через шифрованное соединение
* получение с сервера событий (напр. факт обновления адреса AC)
* заложить возможность 
    * передачи сообщений через server (не дешифруя трафик, но исключая потенциальные атаки с использованием сервера)
    * конференций - передачи сообщени одновременно между несколькими client, желательно исключая сервер
* хранение списка acquainted client на client
* аутентификация на server по своему ключу
* отправка сообщений всем устройствам пользователя
* позже
    * гибкая настройка действий на приходящие сообщения (в зависимости от групп в которые входит источник сообщения, его адреса и т.п.)
    * опциональное шифрование данных хранящихся на client

## Действия ##

### client ###
- при запуске
  - проверяет наличие приватного ключа в формате PKCS12
    - если его нет, то предоставялет пользователю возможность создать такой или загрузить в хранилище программы существующий
  - запрашивает пароль для приватного ключа (если требуется)
    _пароль может быть графическим или иным, надо разобраться что используется для этого на мобильниках_
    пароль можно созранять на устройстве по желанию пользователя (для андроида есть некий механизм аккаунтов стоит посмотреть что там)
  - загружает из настроек список server-ов
  - публикует на каждом server список своих адресов
- при отправке сообщения
  - выбор адресата в GUI (это список своих устройств или контактов пользователей)
  - определяет размер пакета данных
  - определяет возможность соединения // опредление подходящего транспорта и отправка, видимо, должны вполняться в самом транпорте
  - отправка
   - если размер пригоден для одного пакета то отправляет его в UDP одним пакетом
   - иначе отправляет последовательность UDP пакетов
- при определении возможности соединения
  source client (SC) - клиент с которого будет передаваться сообщение
  destination client (DC) - клиент на который будет передаваться сообщение
    - SC определяет может ли он соединиться с известными адресами DC (в случае не удачи или если адреса нет то запрашивает с server текущий адрес DC)
       - инициируется последовательная проверка для каждого транспорта
         например для UDP
          - сначала проверяется локальный адрес
          - затем внешний
  - в случае если сообщение большое для передачи одним пакетом и DC online, но не моджет принимать входящие подключения (NAT и т.п.) 
    то определяет возможность принимать входящие подключения SC
    // полагаем что в таком случае будет иницировано обратное соединение
- при полученеии сообщения
  - если адресат - user, то сообщение рассылается остальным устройствам этого пользователя, после принятия на одном, остальным рассылается извещение о приемке - и сообщение попадает сразу в историю
  - определить устройство
  - на устройстве обработать правила (набор правил должен включать проверку что адресат устройство, т.к. адресовать устройству может только хозяйин или доверенный пользователь, и пользователь доверенный)
  - если правил нет, то известить пользователя и ждать реакции
  - после реакции пользователя разослать остальным устроствам пользователя извещение о передаче исходного сообщения в устройство

## Обмен ##

TODO формат сообщений свой tgbf

взаимодествие client-client и client-server делятся на две категории,
 * одиночные сообщения, посылаются в тех случах когда передаваемые данные помещаются в один пакет (возможно в несколько,
     но не больше чем требуется для настройки потока)
 * сообщения потока - цепочки сообщений в которых важна последовательность и надежность доставки, применятся для
     больших объемов данных (возможно медиапотоков)

## типы сообщений ##

* `BASE`        базовая часть сообщений, содержит:
    * `id(T02)`:  циклический идентификатор (используется для фильтрации дублей, уникален для каждого src)
    * `scr(T16)`: отпечаток CPubK клиента отправителя
    * `dst(T16)`: отпечаток CPubK клиента адресата
* `MSG`     extends `BASE`     единичные сообщения шифрованные CPubK адресата
    * `sign(TB)`: подпись СPrK отправителя
    * `cipher(TH)`: тип кодирования данных: `MSG_CIPHER_NONE = 0` (используется во время CA), `MSG_CIPHER_DST_PUBK = 1`
    * `data(TB)`: зашифрованные CRrK данные, содержат одну из указанных ниже структур
* `STR`    заголовок потока с данными  (подписан CPrK)
    * `streamId(T02)`: идентификатор потока
    * `id(T??)`: последовательный идентификатор пакета в потоке (размер типа можеть меняться с увеличением абсолютного значения)
    * `mac(TB)`: message authentication code (HMAC SHA1) по полю data
    * `data`: данные поточного пакета, структура заголовка `STR_HEAD`, `STR_RESP`, `STR_END`, или данные (TB)
* `RESP`    extends `BASE`  подтверждение приема (ответ на одиночное сообщение, его идентификатор должен быть равен идентификатору исходного сообщения)
    * `code(T01)`: `OK = 1`, `ERROR = 2`, `NOT_AC = 3` (client not acquainted, в ответ на это клиент посылает `CLIENT_IC`), `NOT_AU = 4`  (user not acquainted, тут ничего не поделать),

### Структуры  ###

* `STR_HEAD` - заголовок потока и настройки блочного шифрования
     * `CIPHER_OPTIONS`
         * `cipher(TH)`: тип шифрования, 0-16, пока допустимы значения 0 - без шифрования, и 1 - AES
         * `keyLength(T02)`:  длина ключа
         * `mac(TH)`:     тип генератора mac, 1 - HMAC SHA1
         * `padding(TH)`: тип паддинга, 0-16, пока допустимы значения 0 - без паддинга, и 1 - PKCS5
         * `iv(TB)`:     initialization vector блочного шифра
     * `length(T??)`: беззнаковое число указывающее на размер потока, 0 подразумевает что поток ограничен только пакетом `STR_END`
* `STR_RESP`  - ответ на отправленные пакеты, отправляется на каждые N-пакетов (от 1 до 10, но не реже timeout после
     которого сетевые NAT-устроства могут посчитать udp обмен заврешенным, этот таймаут нужно определять экспериментально),
     содержит идентификаторы принятых пакетов, отправляющая сторона удаляет из буфера все `accepted` пакеты и повторно пересылает `needed`,
     если ранее отправленный пакет отсутствует в `needed` поле из `STR_RESP`, а идущие за ним пакеты уже помечены как принятые то он
     также удаляется из буфера, желательно с записью в лог ПО (т.к. это похоже на ошибку в ПО на принимающей стороне).

     Если один из пакетов был повторно запрошен **более трех** раз то выбрасывается ошибка о проблеме соединения и работа завершается.

     Если после отправки N пактов `STR_RESP` не был получен то то выбрасывается ошибка о проблеме соединения и работа завершается.

     * `accepted(TL(N))`: список принятых пакетов
     * `needed(TL(N))`: список пакетов которые нужно переслать повторно
* `STR_END` по сути структура маркер, должна отправляться в конце потока отправляющей стороной, также может быть послана
    принимающей стороной для прекращения передачи потока
* `CLIENT_ADDR`   публикация адреса клиента на сервере (имеет смысл после публикации CIC) (подписан UPrK)
    * `addrs(TL(N))`: не типизированный список пар ключ - значение, где ключ - типа адреса, значение - адрес
                      известные типы адресов: `IPV4 = (TH)1`, `IPV4 = (TH)2`, т.е. базовые типы адресов хранятся в виде цифр в половинном значении (TH)
                      для произвольных типов стоит выбирать значение типа `TB` и сохранять в нем ASCII обозначение (напр. `tor` или `bluetooth`)
* `CLIENT_IC`  публикация CIC на сервере и отправка CPubK другому client (подписан UPrK)
    * `data(TB)`: CLIENT_SIGNED
        * `user(T16)`: user id
        * `key(TB)`: CPubK
    * `sign(TB)`: подпись UPrK вычисляется по полю data
* `USER_IC`  пользовательская IC
    * `data(TB)`
        * `key(TB)`: UPubK
        * `clients(TL(T16))`: список client id этого пользоватлея (при передаче через qr-code может быть пуст, для уменьшения размера)
    * `sign(TB)`: подпись UPrK вычисляется по полю data
* `ACQUAINTED_USERS`: список известных пользователю пользователей
    * `data(TB)`
        * `users(TL(T16))`: список идентификаторов известных пользователей
    * `sign(TB)`: подпись UPrK вычисляется по полю data
* `DATA`  отправка данных клиенту  - сообщение содержит набор аттрибутов каждый состоит из идентификатора, типа и данных
    в зависимости от типа аттрибут может содержать mime-тип, размер, идентификатор потока, при большом размере
    эта структура может передаваться в потоке
    * `handler(TB)`: идентификатор обработчика (символы из набора \[a-z0-9_-\]), предопределены идентификаторы обработчиков:
        * `save`  - сохраняет данные в настроенный каталог, необходим аттрибут filename
        * `chat` - помещает сообщение во встроенный чат (пока предположим что если эо не текст то отображается кнопка действий и миниатюра куска данных)
        * `show` - отображает данные (практически тоже что и чат, только без истории и сопуствующих аттриубтов)
    * `attrs(TL)`: key-value список, предопределенные ключи имеют тип uint8 (T01), остальные в string (TB). Список
    предопределенных ключей (нумерация начинается с 1): `mime`, `filename`, `encoding` (кодировка для текста, по умолчанию полагается UTF-8)
    * `data(TB)`: собственно данные

    *дополнительные обработчики например могут позволять автоматически открывать приходящие данные (очевидно, это весьма небезопасно,
    но удобно, например показать фотографию с телефона на компьютере)*
* `STR_INIT` extends `SND_BASE` отправка запроса на прередачу шифрованного потока
    * `streamId(T02)`: идентификатор потока
    * `seed(TB)`: набор случайных данных для создания секретного ключа
    * (на будущее) *`ciphers(TL)`: список структур описывающих поддерживаемые методы шифрования*

    *в ответ придет сообщение такого же типа*, с такимже `streamId`, но иным `seed`. По `seed` src и dst генерируется секретный ключ (напр. `sha1(seed1+seed2)`)

single message - сообщение помещающееся в один UPD пакет.

секция DATA в одиночных сообщениях (все `SND_*`) шифруются CPubK адресата и подписываются CPrK отправителя,
 шифрование, очевидно обеспечивает приватность, а подпись выполняет роль аутентификацинного кода.
данные потока (`STR_*`) шифруются блочным шифром, параметры шифрования (в т.ч. секретный ключ определяются на handshake этапе)

## шифрование ##

необходимо предусмотреть возможность выбора типов шифрования и подключения движков шифрования,
ниже указанные обязательные к реализации методы шифрования:

 - ассиметричное для ключей и единичных сообщений - RSA
 - блочное симметричное для потоков - AES (параметризовать длиной ключа, 128 по умолчанию)
    - режим CTR (вектор инициализации генерируется случайно) padding согласно PKCS5Padding
    - формат MAC (HMAC SHA1)