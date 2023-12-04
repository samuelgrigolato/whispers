package io.whispers.jpa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = JpaUserRepository.class)
class JpaUserRepositoryTest extends BaseJpaTest {

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Test
    void shouldCreateUserIfNotExists() {
        assertNull(this.jpaUserRepository.findByUsername("non_existent"));
        this.jpaUserRepository.createIfNotExists("non_existent");
        assertNotNull(this.jpaUserRepository.findByUsername("non_existent"));
    }

    @Test
    @Sql("JpaUserRepositoryTest_shouldIgnoreIfAlreadyExists.sql")
    void shouldIgnoreIfAlreadyExists() {
        assertNotNull(this.jpaUserRepository.findByUsername("existent"));
        this.jpaUserRepository.createIfNotExists("existent");
    }

}
