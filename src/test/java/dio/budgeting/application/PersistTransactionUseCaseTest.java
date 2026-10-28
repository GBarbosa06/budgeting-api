package dio.budgeting.application;

import dio.budgeting.application.input.PersistTransactionInput;
import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersistTransactionUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private PersistTransactionUseCase useCase;

    @Test
    void shouldPersistTransactionAndReturnOutput() {
        var input = new PersistTransactionInput("Gasolina", 10000L, Category.AUTO);

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var output = useCase.execute(input);

        assertThat(output).isNotNull();
        assertThat(output.description()).isEqualTo("Gasolina");
        assertThat(output.category()).isEqualTo("AUTO");
        assertThat(output.value()).isEqualTo(100.00);

        verify(transactionRepository).save(any(Transaction.class));
    }
}
