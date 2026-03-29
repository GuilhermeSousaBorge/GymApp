package backend.auth.usecase;

import backend.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidateTokenUseCaseTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private ValidateTokenUseCase useCase;

    @Test
    void executeShouldReturnTrueWhenProviderValidatesToken() {
        when(jwtTokenProvider.validateToken("ok-token")).thenReturn(true);

        assertTrue(useCase.execute("ok-token"));
    }

    @Test
    void executeShouldReturnFalseWhenProviderRejectsToken() {
        when(jwtTokenProvider.validateToken("bad-token")).thenReturn(false);

        assertFalse(useCase.execute("bad-token"));
    }
}

