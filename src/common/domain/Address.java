package common.domain;

import java.io.Serializable;

/**
 * Почтовый адрес организации.
 * 
 * <p>
 * Содержит единственное поле — {@code zipCode} (индекс).
 * </p>
 */
public class Address implements Serializable {
    private static final long serialVersionUID = 7L;
    private final String zipCode;

    /**
     * Создаёт адрес.
     *
     * @param zipCode почтовый индекс (не может быть null)
     */
    public Address(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getZipCode() {
        return zipCode;
    }

    @Override
    public String toString() {
        return String.format("{zip code: %s}", getZipCode());
    }
}