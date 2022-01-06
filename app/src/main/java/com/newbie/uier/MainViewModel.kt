package com.newbie.uier

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel : ViewModel(){
    val bvidData : MutableLiveData<BaseData<BvidBean>> = MutableLiveData()
    val videoData : MutableLiveData<BaseData<VideoBean>> = MutableLiveData()

    fun requestBvid(bvid : String){
        viewModelScope.launch {
            val result : BaseData<BvidBean> = try {
                BilibiliApiService.getApiService().getVideoList(bvid)
            }catch (e : Exception){
                return@launch
            }
            bvidData.postValue(result)
        }
    }

    fun requestVideo(cid : String, bvid : String){
        viewModelScope.launch {
            val result : BaseData<VideoBean> = try {
                BilibiliApiService.getApiService().getVideo(cid, bvid)
            }catch (e : Exception){
                return@launch
            }
            videoData.postValue(result)
        }
    }
}