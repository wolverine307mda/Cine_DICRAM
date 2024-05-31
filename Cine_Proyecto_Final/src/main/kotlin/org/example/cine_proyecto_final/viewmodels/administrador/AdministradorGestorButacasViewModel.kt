package org.example.cine_proyecto_final.viewmodels.administrador

import javafx.beans.property.SimpleObjectProperty
import org.example.cine_proyecto_final.butacas.models.Butaca
import org.example.cine_proyecto_final.butacas.service.database.ButacaService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging

private val logger = logging()

class AdministradorGestorButacasViewModel : KoinComponent {
    // Inyección de dependencias para ButacaService y ButacaStorageCSV
    private val butacaService: ButacaService by inject()

    // Propiedad de estado que contiene el estado actual de las butacas
    val state: SimpleObjectProperty<ButacaSelectionState> = SimpleObjectProperty(ButacaSelectionState())

    init {
        logger.debug { "Inicializando AdministradorGestorButacasViewModel" }
        state.value.allButacas = butacaService.findAll().value
        state.value.butacas = butacaService.findAll().value
    }

    /**
     * El objeto observable que contiene las butacas
     */
    data class ButacaSelectionState(
        var allButacas: List<Butaca> = emptyList(),
        var butacas: List<Butaca> = emptyList(),
    )
}
