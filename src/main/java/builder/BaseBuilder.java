package builder;


import config.Configuration;

public abstract class BaseBuilder {
    protected final Configuration configuration;

    protected BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
    }
}
