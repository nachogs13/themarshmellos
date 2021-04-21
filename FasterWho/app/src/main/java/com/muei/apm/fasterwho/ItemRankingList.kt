package com.muei.apm.fasterwho

import androidx.fragment.app.Fragment

class ItemRankingList {
    var icons:Int ? = 0
    var title:String ? = null
    var detail:String ? = null
    var detail2:String ? = null

    constructor(icons: Int?, title: String?, detail: String?, detail2: String?)   {
        this.icons = icons
        this.title = title
        this.detail = detail
        this.detail2 = detail2
    }
}