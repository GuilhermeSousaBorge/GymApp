package backend.training.usecase;

import backend.exercise.model.entity.Exercise;
import backend.exercise.model.entity.ExerciseCategory;
import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingProgramExportData;
import backend.training.dto.TrainingProgramPdfFileResponse;
import backend.training.model.entity.TrainingExercise;
import backend.training.model.entity.TrainingProgram;
import backend.training.model.entity.TrainingSheet;
import backend.training.port.TrainingExerciseQueryPort;
import backend.training.port.TrainingProgramPdfExporterPort;
import backend.training.port.TrainingProgramQueryPort;
import backend.training.port.TrainingSheetQueryPort;
import backend.user.model.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExportTrainingProgramPdfUseCaseTest {

    @Mock
    private TrainingProgramQueryPort programQueryPort;

    @Mock
    private TrainingSheetQueryPort sheetQueryPort;

    @Mock
    private TrainingExerciseQueryPort exerciseQueryPort;

    @Mock
    private TrainingProgramPdfExporterPort pdfExporterPort;

    @InjectMocks
    private ExportTrainingProgramPdfUseCase useCase;

    @Test
    void executeShouldGeneratePdfWithRowsAndSlugFileName() {
        User student = User.builder().id(7L).name("Joao da Silva").build();
        TrainingProgram program = TrainingProgram.builder()
                .id(1L)
                .name("Hipertrofia Avancada")
                .student(student)
                .build();

        TrainingSheet sheet = TrainingSheet.builder()
                .id(10L)
                .name("Treino A")
                .orderInProgram(1)
                .build();

        ExerciseCategory category = ExerciseCategory.builder().muscleGroup("Peito").build();
        Exercise exercise = Exercise.builder().name("Supino Reto").equipment("Barra").category(category).build();
        TrainingExercise trainingExercise = TrainingExercise.builder()
                .orderInSheet(1)
                .sets(4)
                .reps("10")
                .restTimeInSeconds(60)
                .techniqueNotes("Cadencia controlada")
                .exercise(exercise)
                .trainingSheet(sheet)
                .build();

        when(programQueryPort.findByIdWithSheets(1L)).thenReturn(Optional.of(program));
        when(sheetQueryPort.countByProgramId(1L)).thenReturn(1);
        when(exerciseQueryPort.findByProgramWithExercise(1L)).thenReturn(List.of(trainingExercise));
        when(pdfExporterPort.generate(any(TrainingProgramExportData.class))).thenReturn(new byte[]{1, 2, 3});

        TrainingProgramPdfFileResponse result = useCase.execute(1L);

        assertEquals("application/pdf", result.getContentType());
        assertEquals("programa-hipertrofia-avancada-joao-da-silva.pdf", result.getFileName());
        assertArrayEquals(new byte[]{1, 2, 3}, result.getContent());

        ArgumentCaptor<TrainingProgramExportData> captor = ArgumentCaptor.forClass(TrainingProgramExportData.class);
        verify(pdfExporterPort).generate(captor.capture());
        TrainingProgramExportData data = captor.getValue();
        assertEquals(1, data.getRows().size());
        assertEquals("Treino A", data.getRows().get(0).getSheetName());
        assertEquals("Supino Reto", data.getRows().get(0).getExerciseName());
    }

    @Test
    void executeShouldGeneratePdfWithEmptyMessageWhenProgramHasNoSheets() {
        User student = User.builder().id(8L).name("Maria").build();
        TrainingProgram program = TrainingProgram.builder()
                .id(2L)
                .name("Basico")
                .student(student)
                .build();

        when(programQueryPort.findByIdWithSheets(2L)).thenReturn(Optional.of(program));
        when(sheetQueryPort.countByProgramId(2L)).thenReturn(0);
        when(exerciseQueryPort.findByProgramWithExercise(2L)).thenReturn(List.of());
        when(pdfExporterPort.generate(any(TrainingProgramExportData.class))).thenReturn(new byte[]{9});

        TrainingProgramPdfFileResponse result = useCase.execute(2L);

        assertEquals("programa-basico-maria.pdf", result.getFileName());
        assertArrayEquals(new byte[]{9}, result.getContent());

        ArgumentCaptor<TrainingProgramExportData> captor = ArgumentCaptor.forClass(TrainingProgramExportData.class);
        verify(pdfExporterPort).generate(captor.capture());
        assertEquals(0, captor.getValue().getRows().size());
        assertEquals("Programa sem folhas de treino cadastradas", captor.getValue().getEmptyMessage());
    }

    @Test
    void executeShouldUseSheetsWithoutExercisesMessageWhenProgramHasSheetsButNoExercise() {
        User student = User.builder().id(9L).name("Pedro").build();
        TrainingProgram program = TrainingProgram.builder()
                .id(3L)
                .name("Resistencia")
                .student(student)
                .build();

        when(programQueryPort.findByIdWithSheets(3L)).thenReturn(Optional.of(program));
        when(sheetQueryPort.countByProgramId(3L)).thenReturn(2);
        when(exerciseQueryPort.findByProgramWithExercise(3L)).thenReturn(List.of());
        when(pdfExporterPort.generate(any(TrainingProgramExportData.class))).thenReturn(new byte[]{7});

        useCase.execute(3L);

        ArgumentCaptor<TrainingProgramExportData> captor = ArgumentCaptor.forClass(TrainingProgramExportData.class);
        verify(pdfExporterPort).generate(captor.capture());
        assertEquals("Programa possui folhas, mas ainda nao ha exercicios cadastrados", captor.getValue().getEmptyMessage());
    }

    @Test
    void executeShouldThrowWhenProgramNotFound() {
        when(programQueryPort.findByIdWithSheets(99L)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(99L));

        assertEquals("Programa nao encontrado", ex.getMessage());
    }
}

