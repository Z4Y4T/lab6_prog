package common.domain;

import java.io.Serializable;

/**
 * Организация, в которой работает сотрудник.
 * 
 * <p>
 * Поля:
 * </p>
 * <ul>
 * <li>{@code employeesCount} — количество сотрудников (может быть null, больше
 * 0)</li>
 * <li>{@code type} — тип организации (обязательное)</li>
 * <li>{@code postalAddress} — почтовый адрес (может быть null)</li>
 * </ul>
 */
public class Organization implements Serializable {
    private static final long serialVersionUID = 6L;
    private final Integer employeesCount;
    private final OrganizationType type;
    private final Address postalAddress;

    /**
     * Создаёт организацию.
     *
     * @param employeesCount количество сотрудников (может быть null)
     * @param type           тип организации (не может быть null)
     * @param postalAddress  почтовый адрес (может быть null)
     */
    public Organization(Integer employeesCount, OrganizationType type, Address postalAddress) {
        this.employeesCount = employeesCount;
        this.type = type;
        this.postalAddress = postalAddress;
    }

    public Integer getEmployeesCount() {
        return employeesCount;
    }

    public OrganizationType getType() {
        return type;
    }

    public Address getPostalAddress() {
        return postalAddress;
    }

    @Override
    public String toString() {
        return String.format("{employees count: %d, type: %s, postal address: %s}",
                getEmployeesCount(),
                type == null ? "null" : type.name(),
                getPostalAddress() != null ? getPostalAddress().toString() : "null");
    }
}