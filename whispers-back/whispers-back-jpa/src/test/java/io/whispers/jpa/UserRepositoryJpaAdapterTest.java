package io.whispers.jpa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = UserRepositoryJpaAdapter.class)
class UserRepositoryJpaAdapterTest extends BaseJpaTest {

    @Autowired
    private UserRepositoryJpa userRepositoryJpa;

    @Autowired
    private UserRepositoryJpaAdapter userRepositoryJpaAdapter;

    @Test
    void shouldCreateUserIfNotExists() {
        assertNull(this.userRepositoryJpa.findByUsername("non_existent"));
        this.userRepositoryJpaAdapter.createIfNotExists("non_existent");
        assertNotNull(this.userRepositoryJpa.findByUsername("non_existent"));
    }

    @Test
    @Sql("UserRepositoryJpaAdapterTest_shouldIgnoreIfAlreadyExists.sql")
    void shouldIgnoreIfAlreadyExists() {
        assertNotNull(this.userRepositoryJpa.findByUsername("existent"));
        this.userRepositoryJpaAdapter.createIfNotExists("existent");
    }

}
