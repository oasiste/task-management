
package com.interview.auth.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
public class User implements Persistable<UUID> {

    @Id
    private UUID id;

    private String email;

    private String passwordHash;

    @Transient
    private boolean isNew = true;

    protected User() {
        this.isNew = false;
    }

    // Used when registering a new user
    public User(
            UUID id,
            String email,
            String passwordHash
    ) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.isNew = true;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void markNotNew() {
        this.isNew = false;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
