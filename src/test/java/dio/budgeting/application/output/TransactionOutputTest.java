package dio.budgeting.application.output;

import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionOutputTest {

    @Test
    void shouldConvertAmountFromCentsToRealCorrectly() {
        var transaction = new Transaction("Mercado Semanal", 8000L, Category.GROCERIES);
        var output = TransactionOutput.from(transaction);

        assertThat(output.id()).isEqualTo(transaction.getId().uuid().toString());
        assertThat(output.description()).isEqualTo("Mercado Semanal");
        assertThat(output.category()).isEqualTo("GROCERIES");
        assertThat(output.value()).isEqualTo(80.00);
    }

    @Test
    void shouldConvertFractionalCentsCorrectly() {
        var transaction = new Transaction("Remédio Gotas", 1250L, Category.PHARMA);
        var output = TransactionOutput.from(transaction);

        assertThat(output.value()).isEqualTo(12.50);

        var cheapItem = new Transaction("Bala", 99L, Category.GROCERIES);
        var cheapOutput = TransactionOutput.from(cheapItem);

        assertThat(cheapOutput.value()).isEqualTo(0.99);
    }
}
