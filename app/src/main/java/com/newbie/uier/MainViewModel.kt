package com.newbie.uier

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel : ViewModel(){
    val videoName : MutableLiveData<BaseData<BvidBean>> = MutableLiveData()

    fun requestBvid(bvid : String){
        viewModelScope.launch {
            val result : BaseData<BvidBean> = try {
                BilibiliApiService.getApiService().getVideo(bvid)
            }catch (e : Exception){
                return@launch
            }
            videoName.postValue(result)
        }
    }

    fun requestCvid(aid : String){
        viewModelScope.launch {
            val result = try {
                BilibiliApiService.getApiService().getVideo(aid)
            }catch (e : Exception){
            }
//            videoName.value = result.toString()
        }
    }


}