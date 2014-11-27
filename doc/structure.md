## structure ##

* crypto service - хранит внутри приватный ключ клиента и пользвателя
    * code(options{}) - шифрует и дешифрует
    * sign - сервис подписки
    * symmetric
    * mac - сервис генерации MAC

*ACQUAINTED_USERS*

* map uid ->
    * UIC
    * list clients ->
        * cid

*ACQUAINTED_CLIENTS*

* map cid ->
    * uid
    * cic
    * client_address