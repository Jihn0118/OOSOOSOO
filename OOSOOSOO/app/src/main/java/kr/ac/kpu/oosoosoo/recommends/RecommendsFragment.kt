package kr.ac.kpu.oosoosoo.recommends

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.core.Amplify
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_home_popular_contents.*
import kotlinx.android.synthetic.main.fragment_recommends.*
import kotlinx.android.synthetic.main.recy_item_recommend_card.*
import kotlinx.android.synthetic.main.recy_item_recommend_card.view.*
import kr.ac.kpu.oosoosoo.R
import kr.ac.kpu.oosoosoo.adapters.RecommendContentListAdapter
import kr.ac.kpu.oosoosoo.connection.RetrofitBuilder
import kr.ac.kpu.oosoosoo.contents.ContentInfo
import kr.ac.kpu.oosoosoo.contents.RecommendContentInfo
import kr.ac.kpu.oosoosoo.search.SearchActivity
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.max

class TrendsFragment : Fragment() {
    val recommendContentsArrayList : MutableList<ContentInfo> = ArrayList() //모든 컨텐츠 리스트

    companion object {
        const val MAX_NUM = 30
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recommends, container, false)

        //홈화면 검색 돋보기 버튼
        view.btn_home_search.setOnClickListener {
            requireContext().startActivity<SearchActivity>()
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        val call = RetrofitBuilder().callRecommendContents

        recommend_cardList_recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        var input = HashMap<String, String>()
        input["email"] = Amplify.Auth.currentUser.username

        var recommendListAdapter : RecommendContentListAdapter



        //추천 컨텐츠 출력
        call.getRecommendContents(input).enqueue(object : Callback<List<RecommendContentInfo>> {
            override fun onResponse(
                call: Call<List<RecommendContentInfo>>,
                response: Response<List<RecommendContentInfo>>
            ) {
                val recommenededContentsList = response.body()

                Log.d("recommend_contents", "통신 성공")

                if (recommenededContentsList != null) {
                    for (recommendedContent in recommenededContentsList) {
                        recommendContentsArrayList.add(recommendedContent.recommend!!)
                    }
                }

                recommendListAdapter = RecommendContentListAdapter(context!!, ArrayList(recommendContentsArrayList))

                recommend_cardList_recyclerView.adapter = recommendListAdapter
                recommend_cardList_recyclerView.adapter!!.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<List<RecommendContentInfo>>, t: Throwable) {
                Log.d("home_recommend_contents", t.message.toString())
            }
        })

        recommend_cardList_recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    var firstPos = (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                    var secondPos = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                    var selectedPos = max(firstPos,secondPos)
                    if(selectedPos!=-1){
                        var viewItem = (recyclerView.layoutManager as LinearLayoutManager).findViewByPosition(selectedPos)
                        viewItem?.run{
                            var itemMargin = (recyclerView.measuredWidth-viewItem.measuredWidth)/2
                            recyclerView.smoothScrollBy( this.x.toInt()-itemMargin , 0)
                        }
                    }
                }
            }
        })
    }
}