package managers;

import utilities.Factory;

public class FactoryManager {
    private Factory currentFactory;

    public FactoryManager(Factory factory) {
        this.currentFactory = factory;
    }

    public Factory getFactory() {
        return currentFactory;
    }

    public void setFactory(Factory factory) {
        this.currentFactory = factory;
    }
}