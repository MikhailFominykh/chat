chat
====
Перед запуском выполнить mvn install

Запуск сервера из папки server:
  mvn exec:java -Dexec.mainClass="test.chat.server.Main" -Dchat.port=<port>

Запуск клиента из папки client:
  mvn exec:java -Dexec.mainClass="test.chat.client.Main" -Dchat.port=<port>

  Доступные команды:
    /name <имя> -- смена имени
    /kick <имя> -- удалить пользователя с указанным именем
    /usercount -- количество пользователей в чате
    /users -- список пользователей в чате

Запуск ботов для стресс-теста из папки client:
  mvn exec:java -Dexec.mainClass="test.chat.client.stress.Main" -Dchat.port=<port>

  Доступные параметры для стресс-теста:
    -Dbot.count=<количество ботов>
    -Dbot.threads=<количество потоков>
    -Dbot.minLifeTime=<минимальное время жизни бота в секундах>
    -Dbot.maxLifeTime=<максимальное время жизни бота в секундах>
    -Dbot.minMessageInterval=<минимальный интервал между сообщениями бота в секундах>
    -Dbot.maxMessageInterval=<максимальный интервал между сообщениями бота в секундах>
    -Dbot.messageProbability=<вероятность отправки сообщения> -- значение в интервале [0..1]

Установка уровня логирования:
  -Dlog4j2.level=<level>
