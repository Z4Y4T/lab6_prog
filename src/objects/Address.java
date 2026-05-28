package objects;

public class Address {
    private final String zipCode;

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