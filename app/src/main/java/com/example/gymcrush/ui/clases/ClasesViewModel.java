package com.example.gymcrush.ui.clases;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.gymcrush.data.remote.dto.HorarioResponseDto;
import com.example.gymcrush.data.remote.dto.ReservaResponseDto;
import com.example.gymcrush.data.repository.ReservasRepository;
import java.util.ArrayList;
import java.util.List;

public class ClasesViewModel extends ViewModel {

    private final ReservasRepository repository;
    private final MutableLiveData<List<HorarioResponseDto>> horarios = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public ClasesViewModel() {
        this.repository = ReservasRepository.getInstance();
    }

    public LiveData<List<HorarioResponseDto>> getHorarios() { return horarios; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getLoading() { return loading; }

    public void cargarHorariosPorDia(String dia) {
        loading.setValue(true);
        repository.getHorariosPorDia(dia, new ReservasRepository.RepositoryCallback<List<HorarioResponseDto>>() {
            @Override
            public void onSuccess(List<HorarioResponseDto> listaHorarios) {
                if (listaHorarios == null || listaHorarios.isEmpty()) {
                    horarios.setValue(new ArrayList<>());
                    loading.setValue(false);
                    return;
                }

                final int[] respondidos = {0};
                for (HorarioResponseDto h : listaHorarios) {
                    repository.getAsistentesClase(h.getId(), new ReservasRepository.RepositoryCallback<List<ReservaResponseDto>>() {
                        @Override
                        public void onSuccess(List<ReservaResponseDto> asistentes) {
                            if (asistentes != null) {
                                h.setOcupacion(asistentes.size());
                            }
                            checkFinalizado();
                        }

                        @Override
                        public void onError(String errorMessage) {
                            checkFinalizado();
                        }

                        private void checkFinalizado() {
                            respondidos[0]++;
                            if (respondidos[0] == listaHorarios.size()) {
                                horarios.setValue(listaHorarios);
                                loading.setValue(false);
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
                loading.setValue(false);
            }
        });
    }

    public void eliminarHorarioLocal(Long id) {
        List<HorarioResponseDto> actual = horarios.getValue();
        if (actual != null) {
            List<HorarioResponseDto> nuevaLista = new ArrayList<>(actual);
            nuevaLista.removeIf(h -> h.getId().equals(id));
            horarios.setValue(nuevaLista);
        }
    }

    public void realizarReserva(Long usuarioId, Long horarioId, ReservasRepository.RepositoryCallback<ReservaResponseDto> callback) {
        repository.crearReserva(usuarioId, horarioId, new ReservasRepository.RepositoryCallback<ReservaResponseDto>() {
            @Override
            public void onSuccess(ReservaResponseDto result) {
                // Actualización optimista: Incrementamos la ocupación localmente de inmediato
                List<HorarioResponseDto> listaActual = horarios.getValue();
                if (listaActual != null) {
                    for (HorarioResponseDto h : listaActual) {
                        if (h.getId().equals(horarioId)) {
                            int ocupacionActual = h.getOcupacion();
                            h.setOcupacion(ocupacionActual + 1);
                            break;
                        }
                    }
                    // Notificamos a los observadores para que la UI se actualice al instante
                    horarios.setValue(listaActual);
                }
                callback.onSuccess(result);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
}