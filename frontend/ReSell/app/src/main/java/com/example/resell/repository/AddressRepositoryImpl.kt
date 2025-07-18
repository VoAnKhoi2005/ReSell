package com.example.resell.repository

import arrow.core.Either
import com.example.resell.network.ApiService
import com.example.resell.network.NetworkError
import com.example.resell.network.toNetworkError
import com.example.resell.model.Address
import com.example.resell.model.CreateAddressRequest
import com.example.resell.model.DeleteAddressesRequest
import com.example.resell.model.District
import com.example.resell.model.Province
import com.example.resell.model.UpdateAddressRequest
import com.example.resell.model.Ward
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddressRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): AddressRepository {
    override suspend fun createAddress(
        fullName: String,
        phone: String,
        wardID: String,
        detail: String,
        isDefault: Boolean
    ): Either<NetworkError, Boolean> {
        return Either.catch {
            val request = CreateAddressRequest(
                fullName = fullName,
                phone = phone,
                wardID = wardID,
                detail = detail,
                isDefault = isDefault
            )

            apiService.createAddress(request)
        }.mapLeft { it.toNetworkError() }
    }


    override suspend fun getAddressByID(addressID: String): Either<NetworkError, Address> {
        return Either.catch {
            apiService.getAddressByID(addressID)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getAddressByUserID(userID: String): Either<NetworkError, List<Address>> {
        return Either.catch {
            apiService.getAddressByUserID(userID)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getDefaultAddress(): Either<NetworkError, Address> {
        return Either.catch {
            apiService.getDefaultAddress()
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getAllProvinces(): Either<NetworkError, List<Province>> {
        return Either.catch {
            apiService.getAllProvinces()
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getDistricts(provinceID: String): Either<NetworkError, List<District>> {
        return Either.catch {
            apiService.getDistricts(provinceID)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getWards(districtID: String): Either<NetworkError, List<Ward>> {
        return Either.catch {
            apiService.getWards(districtID)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun updateAddress(
        addressID: String,
        request: UpdateAddressRequest
    ): Either<NetworkError, Boolean> {
        return Either.catch {
            apiService.updateAddress(addressID, request)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun deleteAddress(addressID: String): Either<NetworkError, Boolean> {
        return Either.catch {
            apiService.deleteAddress(addressID)
        }.mapLeft { it.toNetworkError() }
    }

    override suspend fun deleteAddresses(addressIDs: List<String>): Either<NetworkError, Boolean> {
        return Either.catch {
            apiService.deleteAddresses(DeleteAddressesRequest(addressIDs))
        }.mapLeft { it.toNetworkError() }
    }
}