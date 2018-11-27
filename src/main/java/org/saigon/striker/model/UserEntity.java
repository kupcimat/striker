package org.saigon.striker.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

@Document(collection = "user")
public class UserEntity {

    @Id
    private final String id;
    @Indexed
    private final String username;
    private final String password;

    public static UserEntity of(User user) {
        notNull(user);
        return new UserEntity(null, user.getUsername(), user.getPassword());
    }

    public static UserEntity of(String username, String password) {
        notEmpty(username);
        notEmpty(password);
        return new UserEntity(null, username, password);
    }

    public UserEntity(String id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public User toUser() {
        return new User(this.username, this.password);
    }

    public UserEntity withId(String id) {
        return new UserEntity(id, this.username, this.password);
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
