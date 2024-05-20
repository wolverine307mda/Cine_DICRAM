package org.example.cine_proyecto_final.butacas.service.database

import com.github.michaelbull.result.*
import org.example.cine_proyecto_final.butacas.errors.ButacaError
import org.example.cine_proyecto_final.butacas.models.Butaca
import org.example.cine_proyecto_final.butacas.repository.ButacaRepository
import org.example.cine_proyecto_final.butacas.validator.ButacaValidator
import org.example.cine_proyecto_final.config.AppConfig
import java.time.LocalDateTime

/**
 * Implementación de los servicios relacionados con las butacas.
 *
 * @property butacaRepositorio El repositorio de butacas para acceder a la base de datos.
 * @property butacaValidator El validador de butacas para validar los datos importados.
 * @property config La configuración de la aplicación.
 */
class ButacaServiceImpl(
    private var butacaRepositorio: ButacaRepository,
    private var butacaValidator: ButacaValidator,
    private val config : AppConfig
) : ButacaService {

    /**
     * Busca y devuelve todas las butacas en la base de datos.
     *
     * @return Un resultado que contiene una lista de objetos Butaca si la operación tiene éxito,
     * o un error de ButacaError si no se encuentran butacas.
     */
    override fun findAll(): Result<List<Butaca>, ButacaError> {
        val result = butacaRepositorio.findAll()
        if (result.isNotEmpty()) return Ok(result)
        else return Err(ButacaError.ButacaStorageError("No hay ninguna butaca en la base de datos"))
    }


    /**
     * Busca y devuelve una butaca en la base de datos.
     *
     * @param id El ID de la butaca a buscar.
     * @return Un resultado que contiene una butaca si la operación tiene éxito,
     */
    override fun findById(id: String): Result<Butaca, ButacaError> {
        butacaRepositorio.findById(id)?.let {
            return Ok(it)
        }
        return Err(ButacaError.ButacaNotFoundError("La butaca con ID $id no existe"))
    }


    /**
     * Crea una butaca en la base de datos.
     *
     * @param butaca La butaca a crear.
     */
    override fun update(id: String, butaca: Butaca): Result<Butaca, ButacaError> {
        butacaValidator.validate(butaca).onSuccess {
            butacaRepositorio.update(id = id, butaca = butaca)?.let {
                return Ok(it)
            }
        }.onFailure {
            return Err(ButacaError.ButacaInvalida("La butaca con id= ${butaca.id} no es válida"))
        }
        return Err(ButacaError.ButacaStorageError("La butaca con id: ${butaca.id} no se pudo guardar"))
    }

    /**
     * Busca y devuelve todas las butacas en la base de datos basandose en la fecha.
     *
     * @return Un resultado que contiene una lista de objetos Butaca si la operación tiene éxito,
     * o un error de ButacaError si no se encuentran butacas.
     */
    override fun findAllByDate(date: LocalDateTime): Result<List<Butaca>, ButacaError> {
        val result = butacaRepositorio.findAllBasedOnDate(date)
        if (result.isNotEmpty()) return Ok(result)
        else return Err(ButacaError.ButacaStorageError("No hay ninguna butaca en la base de datos"))
    }
}