package objects;

public class Organization {
    private final Integer employeesCount;
    private final OrganizationType type;
    private final Address postalAddress;

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
        return String.format("{employees count: %d, type: %s, postal address: %s}", getEmployeesCount(), type.name(),
                getPostalAddress());
    }
}