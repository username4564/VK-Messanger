package com.qto.ru.vkmessanger.vk;

/**
 * Используется для хранения информации
 * о диалоге
 */
public class VkDialog {
    /** Пользователь, который является собеседником */
    private VkUser mUser;
    /** Последнее сообщение находящееся в диалоге */
    private VkMessage mMessage;

    /**
     * Конструктор диалога, инициализирует поле <code>message</code>
     * @param message
     * Последнее сообщение диалога
     */
    public VkDialog(VkMessage message){
        mMessage = message;
    }

    /**
     * Получает последнее сообщение диалога
     * @return
     * Последнее сообщение диалога
     */
    public VkMessage getMessage() {
        return mMessage;
    }

    /**
     * Устанавливает последнее сообщение диалога
     * @param message
     * Последнее сообщение диалога
     */
    public void setMessage(VkMessage message) {
        mMessage = message;
    }

    /**
     * Получает пользователя, который является
     * собеседником
     * @return
     * Пользователь - собеседник
     */
    public VkUser getUser() {
        return mUser;
    }

    /**
     * Устанавливает пользователя, который является
     * собеседником
     * @param user
     * Пользователь - собеседник
     */
    public void setUser(VkUser user) {
        mUser = user;
    }

}
