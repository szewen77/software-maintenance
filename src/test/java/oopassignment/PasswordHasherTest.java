package oopassignment;

import oopassignment.util.PasswordHasher;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PasswordHasherTest {

    private PasswordHasher hasher;

    @Before
    public void setUp() {
        hasher = new PasswordHasher();
    }

    @Test
    public void hashProducesSameValueForSameInput() {
        String hash1 = hasher.hash("password");
        String hash2 = hasher.hash("password");
        
        // SHA-256 is deterministic (no salt in this implementation)
        assertEquals("Same password should produce same hash", hash1, hash2);
    }

    @Test
    public void hashIsNotEmpty() {
        String hash = hasher.hash("test");
        assertNotNull("Hash should not be null", hash);
        assertFalse("Hash should not be empty", hash.isEmpty());
    }

    @Test
    public void hashCorrectPassword() {
        String password = "mypassword";
        String hash1 = hasher.hash(password);
        String hash2 = hasher.hash(password);
        
        assertEquals("Same password should produce same hash", hash1, hash2);
    }

    @Test
    public void hashDifferentPasswords() {
        String hash1 = hasher.hash("correct");
        String hash2 = hasher.hash("wrong");
        
        assertNotEquals("Different passwords should produce different hashes", hash1, hash2);
    }

    @Test
    public void hashEmptyPassword() {
        String hash = hasher.hash("");
        
        assertNotNull("Should hash empty password", hash);
        assertFalse("Hash should not be empty", hash.isEmpty());
    }

    @Test
    public void hashLongPassword() {
        String longPassword = "a".repeat(1000);
        String hash = hasher.hash(longPassword);
        
        assertNotNull("Should hash long password", hash);
        assertFalse("Hash should not be empty", hash.isEmpty());
    }

    @Test
    public void hashSpecialCharacters() {
        String specialPassword = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
        String hash = hasher.hash(specialPassword);
        
        assertNotNull("Should hash special characters", hash);
        assertFalse("Hash should not be empty", hash.isEmpty());
    }

    @Test
    public void hashUnicodeCharacters() {
        String unicodePassword = "密码🔐";
        String hash = hasher.hash(unicodePassword);
        
        assertNotNull("Should hash unicode characters", hash);
        assertFalse("Hash should not be empty", hash.isEmpty());
    }

    @Test
    public void hashIsDeterministic() {
        String password = "test123";
        String hash1 = hasher.hash(password);
        String hash2 = hasher.hash(password);
        String hash3 = hasher.hash(password);
        
        // All hashes should be identical
        assertEquals(hash1, hash2);
        assertEquals(hash2, hash3);
    }

    @Test
    public void differentPasswordsProduceDifferentHashes() {
        String hash1 = hasher.hash("password1");
        String hash2 = hasher.hash("password2");
        
        assertNotEquals("Different passwords should produce different hashes", hash1, hash2);
    }

    @Test
    public void hashProducesHexString() {
        String hash = hasher.hash("test");
        
        // Hash should contain only hex characters (lowercase)
        assertTrue("Hash should be hex string", 
                hash.matches("^[a-f0-9]+$"));
    }

    @Test
    public void hashLengthIsConsistent() {
        String hash1 = hasher.hash("short");
        String hash2 = hasher.hash("a very long password with many characters");
        
        assertEquals("SHA-256 hashes should have same length", hash1.length(), hash2.length());
        assertEquals("SHA-256 produces 64 character hex string", 64, hash1.length());
    }

    @Test
    public void hashCaseSensitive() {
        String hash1 = hasher.hash("Password");
        String hash2 = hasher.hash("password");
        
        assertNotEquals("Hash should be case sensitive", hash1, hash2);
    }

    @Test
    public void hashWhitespace() {
        String hash1 = hasher.hash("password");
        String hash2 = hasher.hash("password ");
        String hash3 = hasher.hash(" password");
        
        assertNotEquals("Trailing space should produce different hash", hash1, hash2);
        assertNotEquals("Leading space should produce different hash", hash1, hash3);
    }
}

