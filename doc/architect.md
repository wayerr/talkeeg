# Проект takeeg #

## Архитектура ##

[протокол обмена](protocol.md)

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
    * упаковка identity card в qr-code (надо определить размер, возможно поместится только список адресов и отпечаток публичного ключа)
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
  - проверяет наличие приватного ключей в формате PKCS8 и X509
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


## шифрование ##

необходимо предусмотреть возможность выбора типов шифрования и подключения движков шифрования,
ниже указанные обязательные к реализации методы шифрования:

 - ассиметричное для ключей и единичных сообщений - RSA/ECB/PKCS1Padding
 - блочное симметричное для потоков - AES (параметризовать длиной ключа, 128 по умолчанию)
    - режим CTR (вектор инициализации генерируется случайно) padding согласно PKCS5Padding
    - формат MAC (HMAC SHA1)