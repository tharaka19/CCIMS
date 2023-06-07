package com.cims.project.utils;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

/**
 * SnowflakeIdGenerator is a Hibernate IdentifierGenerator that generates unique identifiers using a Snowflake algorithm.
 * The generated identifiers consist of a timestamp and a sequence number, ensuring that identifiers generated at the same
 * time on different machines are unique.
 */
public class SnowflakeIdGenerator implements IdentifierGenerator {

    private Snowflake snowflake = new Snowflake();

    public SnowflakeIdGenerator() {
    }

    /**
     * Generates a new unique identifier using a Snowflake algorithm.
     *
     * @param sharedSessionContractImplementor the session implementation
     * @param o                                the entity object for which the identifier is being generated
     * @return a unique Serializable identifier for the entity object
     * @throws HibernateException if there is an error generating the identifier
     */
    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
        return Long.valueOf(this.snowflake.newId());
    }
}
