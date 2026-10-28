package dio.budgeting.application;

import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListTransactionsByCategoryUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private ListTransactionsByCategoryUseCase useCase;

    @Test
    void shouldReturnTransactionsByCategory() {
        var tx1 = new Transaction("Supermercado 1", 5000L, Category.GROCERIES);
        var tx2 = new Transaction("Supermercado 2", 7500L, Category.GROCERIES);

        when(transactionRepository.findAllByCategory(Category.GROCERIES)).thenReturn(List.of(tx1, tx2));

        var result = useCase.execute(Category.GROCERIES);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).description()).isEqualTo("Supermercado 1");
        assertThat(result.get(0).value()).isEqualTo(50.00);
        assertThat(result.get(1).description()).isEqualTo("Supermercado 2");
        assertThat(result.get(1).value()).isEqualTo(75.00);

        verify(transactionRepository).findAllByCategory(Category.GROCERIES);
    }
}
