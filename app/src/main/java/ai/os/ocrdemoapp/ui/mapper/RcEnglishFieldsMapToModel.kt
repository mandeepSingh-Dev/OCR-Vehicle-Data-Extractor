package ai.os.ocrdemoapp.ui.mapper

import ai.os.ocrdemoapp.RcEnglishDetails
import ai.os.ocrdemoapp.ui.NewRCEnglishFields
import ai.os.ocrdemoapp.ui.OldRCEnglishFields
import ai.os.ocrdemoapp.ui.model.NewRCDataItem
import ai.os.ocrdemoapp.ui.model.OldRCDataItem
import android.util.Log

fun RcEnglishDetails?.toDataClass(isNew : Boolean = true): Any {

    if(isNew){

    val newRCDataItem = NewRCDataItem()

    this?.forEach{
        Log.d("flbmkfbnf", "${it.key}  -> ${it.value}")
        when(it.key){
            NewRCEnglishFields.NO_PENDAFTARAN  -> {newRCDataItem.registrationNo = it.value}
            NewRCEnglishFields.NO_ID -> {newRCDataItem.noId = it.value}
            NewRCEnglishFields.NAMA_PEMUNYA_BERDAFTAR -> {newRCDataItem.ownerName = it.value}
            NewRCEnglishFields.ALAMAT -> {newRCDataItem.address = it.value}
            NewRCEnglishFields.NO_CHASIS -> {newRCDataItem.chassisNo = it.value}
            NewRCEnglishFields.NO_ENJIN  -> {newRCDataItem.engineNo= it.value}
            NewRCEnglishFields.KEUPAYAAN_ENJIN -> {newRCDataItem.engineCapacity = it.value}
            NewRCEnglishFields.BUATAN_NAMA_MODEL -> {newRCDataItem.manufacturer = it.value}
            NewRCEnglishFields.BAHAN_BAKAR -> {newRCDataItem.fuelType = it.value}
            NewRCEnglishFields.STATUS_ASAL  ->{newRCDataItem.originalStatus = it.value}
            NewRCEnglishFields.KELAS_KEGUNAAN -> {newRCDataItem.vehicleUsageClass = it.value}
            NewRCEnglishFields.JENIS_BADAN_TAHUN_DIBUAT -> {newRCDataItem.bodyTypeYearManufactured = it.value}
            NewRCEnglishFields.TARIKH_PENDAFTARAN -> {newRCDataItem.registrationDate = it.value}
            NewRCEnglishFields.BDM_BGK_BTM -> {newRCDataItem.bdmBgkBtm = it.value}
        }
    }
        return newRCDataItem
    }
else{

        val oldRCDataItem = OldRCDataItem()

        this?.forEach{
            Log.d("flbmkfbnf", "${it.key}  -> ${it.value}")
            when(it.key){
                OldRCEnglishFields.NAMA_PEMUNYA  -> {oldRCDataItem.ownerName = it.value}
               OldRCEnglishFields.NO_ENJIN -> {oldRCDataItem.engineNo = it.value}
               OldRCEnglishFields.NO_CASIS -> {oldRCDataItem.chassisNo = it.value}
               OldRCEnglishFields.ALAMAT -> {oldRCDataItem.address = it.value}
               OldRCEnglishFields.BUATAN -> {oldRCDataItem.manufacturer = it.value}
               OldRCEnglishFields.NAMA_MODEL  -> {oldRCDataItem.modelName = it.value}
               OldRCEnglishFields.KEUPAYAAN_ENJIN -> {oldRCDataItem.engineCapacity = it.value}
               OldRCEnglishFields.BAHAN_BAKAR -> {oldRCDataItem.fuelType = it.value}
               OldRCEnglishFields.WARNA -> {oldRCDataItem.color = it.value}
               OldRCEnglishFields.KELAS_KEGUNAAN  ->{oldRCDataItem.usageClass = it.value}
               OldRCEnglishFields.JENIS_BADAN -> {oldRCDataItem.bodyType = it.value}
               OldRCEnglishFields.TAHUN_DIBUAT -> {oldRCDataItem.yearManufactured = it.value}
               OldRCEnglishFields.TARIKH_PENDAFTARAN -> {oldRCDataItem.registrationDate = it.value}
               OldRCEnglishFields.STATUS_PEMUNYA -> {oldRCDataItem.ownerStatus = it.value}
               OldRCEnglishFields.MUATAN_TEMPAT -> {oldRCDataItem.seatingCapacity = it.value}
               OldRCEnglishFields.DUDUK -> {oldRCDataItem.seats = it.value}
               OldRCEnglishFields.KADAR_LESEN -> {oldRCDataItem.licenseFee = it.value}
               OldRCEnglishFields.KENDERAAN_MOTOR -> {oldRCDataItem.motorVehicle = it.value}
            }
        }
        return oldRCDataItem
}
}