package org.example.cine_proyecto_final.cuentas.service.database

import com.github.michaelbull.result.*
import cuenta.errors.CuentaError
import org.example.cine_proyecto_final.cuentas.models.Cuenta
import org.example.cine_proyecto_final.cuentas.repository.CuentaRepository
import org.example.cine_proyecto_final.cuentas.service.cache.CuentaCache
import org.example.cine_proyecto_final.cuentas.validator.CuentaValidator
import org.lighthousegames.logging.logging

private val logger = logging()

/**
 * Implementación de [CuentaServicio] que interactúa con el repositorio de cuentas para realizar operaciones.
 * @property cuentaRepository El repositorio de cuentas utilizado para acceder y manipular datos de cuentas.
 */
class CuentaServicioImpl(
    private var cuentaRepository: CuentaRepository,
    private var cuentaValidator: CuentaValidator,
    private val cache : CuentaCache
): CuentaServicio {



    /**
     * Busca una cuenta de usuario por su identificador único.
     * @param email El identificador único de la cuenta de usuario a buscar.
     * @return un [Result] que contiene la cuenta de usuario encontrada o un error [CuentaError].
     */
    override fun findByEmail(email: String): Result<Cuenta, CuentaError> {
        logger.debug { "Buscando cuenta con email: $email" }
        cache.get(email)?.let {
            cache.put(it.email,it)
            return Ok(it)
        } ?: cuentaRepository.findById(email)?.let {
            cache.put(it.email,it)
            return Ok(it)
        }
        return Err(CuentaError.CuentaStorageError("No se pudo encontrar la cuenta con el email: $email"))
    }

    /**
     * Guarda una nueva cuenta de usuario.
     * @param cuenta La cuenta de usuario a guardar.
     * @return un [Result] que contiene la cuenta de usuario guardada o un error [CuentaError].
     */
    override fun save(cuenta: Cuenta): Result<Cuenta, CuentaError> {
        logger.debug { "Guardando cuenta con email: ${cuenta.email}" }
        cuentaValidator.validate(cuenta).onSuccess{
            cache.get(cuenta.email) ?:
            cuentaRepository.save(cuenta)?.let {
                cache.put(it.email,it)
                return Ok(it)
            }
        }
        return Err(CuentaError.CuentaStorageError("La cuenta con el email: ${cuenta.email} no se pudo guardar"))
    }

    /**
     * Actualiza una cuenta de usuario existente.
     * @param email El identificador único de la cuenta de usuario a actualizar.
     * @param cuenta La cuenta de usuario a actualizar.
     * @return un [Result] que contiene la cuenta de usuario actualizada o un error [CuentaError].
     */
    override fun update(email: String, cuenta: Cuenta): Result<Cuenta, CuentaError> {
        logger.debug { "Actualizando cuenta con email: ${cuenta.email}" }
        cuentaValidator.validate(cuenta).onSuccess {
            cache.get(email)?.let {
                cache.put(it.email, cuenta)
                cuentaRepository.update(email, cuenta)
                return Ok(it)
            } ?: cuentaRepository.update(email, cuenta)?.let {
                cache.put(it.email,it)
                return Ok(it)
            }
        }
        return Err(CuentaError.CuentaStorageError("La cuenta con el email: ${cuenta.email} no se pudo actualizar"))
    }

    /**
     * Busca todas las cuentas de usuario en la base de datos.
     * @return un [Result] que contiene la lista de usuarios o un error [CuentaError].
     */
    override fun findAll(): Result<List<Cuenta>, CuentaError> {
        return Ok(cuentaRepository.findAll())
    }
}