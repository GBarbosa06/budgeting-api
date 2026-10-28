package dio.budgeting.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionTest {

    @Test
    void shouldCreateTransactionWithGeneratedId() {
        var transaction = new Transaction("Supermercado", 15000L, Category.GROCERIES);

        assertThat(transaction.getId()).isNotNull();
        assertThat(transaction.getId().uuid()).isNotNull();
        assertThat(transaction.getDescription()).isEqualTo("Supermercado");
        assertThat(transaction.getAmount()).isEqualTo(15000L);
        assertThat(transaction.getCategory()).isEqualTo(Category.GROCERIES);
    }

    @Test
    void shouldCreateTransactionWithProvidedId() {
        var id = new TransactionId(UUID.randomUUID());
        var transaction = new Transaction(id, "Remédios", 4500L, Category.PHARMA);

        assertThat(transaction.getId()).isEqualTo(id);
        assertThat(transaction.getDescription()).isEqualTo("Remédios");
        assertThat(transaction.getAmount()).isEqualTo(4500L);
        assertThat(transaction.getCategory()).isEqualTo(Category.PHARMA);
    }
}
