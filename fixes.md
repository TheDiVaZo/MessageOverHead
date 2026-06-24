Слабые Места

Публичная API-часть пока незавершенная. BubbleProfileRegistry существует, но нигде не используется; реальный профиль создается прямо внутри тестовой команды: BubbleTestCommand.java.

BukkitBubbleScheduler создает отдельную Bukkit-задачу на каждый bubble: BukkitBubbleScheduler.java. Для малого числа пузырей нормально, но при массовом чате лучше один общий tick-loop.

Thread model не зафиксирован API-контрактом. Контейнеры и registry в основном не thread-safe, что нормально для Bukkit main thread, но это нужно явно документировать или защищать.