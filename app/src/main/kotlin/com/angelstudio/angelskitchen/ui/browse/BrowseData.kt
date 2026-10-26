package com.angelstudio.angelskitchen.ui.browse

import androidx.annotation.StringRes
import androidx.annotation.DrawableRes
import com.angelstudio.angelskitchen.R
import com.angelstudio.angelskitchen.ui.theme.KitchenLayout
import com.angelstudio.angelskitchen.ui.theme.KitchenSizes
import com.angelstudio.angelskitchen.ui.theme.KitchenSpacing
import kotlin.math.floor

data class BrowseCategory(
    val apiName: String,
    val imageUrl: String,
    val description: String,
)

fun filterCategories(categories: List<BrowseCategory>, query: String): List<BrowseCategory> {
    val term = query.trim()
    return if (term.isEmpty()) categories else categories.filter {
        it.apiName.contains(term, ignoreCase = true)
    }
}

enum class BrowseRegion(@param:StringRes val label: Int) {
    All(R.string.browse_region_all),
    Europe(R.string.browse_region_europe),
    Asia(R.string.browse_region_asia),
    Americas(R.string.browse_region_americas),
    Africa(R.string.browse_region_africa),
    Oceania(R.string.browse_region_oceania),
}

data class BrowseCountry(
    val apiArea: String,
    @param:StringRes val name: Int,
    @param:StringRes val description: Int,
    val flag: String,
    val region: BrowseRegion,
    val countryCode: String = apiArea,
    @param:DrawableRes val flagResource: Int = countryFlagResource(apiArea),
)

// Preserve legacy routes where possible; country names cover missing adjective results.
// Keep ISO identities independent of API route spellings.
internal val supportedCountryAreas = setOf(
    "italian", "france", "japanese", "mexican", "india", "spanish", "thai", "moroccan",
    "united states", "british", "canadian", "jamaican", "chinese", "netherlands", "egyptian",
    "greek", "irish", "kenyan", "malaysian", "croatian", "norway", "portuguese", "russian",
    "argentina", "slovakia", "vietnamese", "turkish", "syrian", "algerian", "tunisian", "polish",
    "ukrainian", "filipino", "australian", "saudi arabian", "laos", "afghanistan", "albania",
    "andorra", "angola", "antigua and barbuda", "armenia", "aruba", "austria", "azerbaijan",
    "bahamas", "bangladesh", "barbados", "belgium", "botswana", "brazil", "bulgaria", "cambodia",
    "cayman islands", "chile", "colombia", "costa rica", "cuba", "denmark", "dominica", "estonia",
    "uruguay", "venezuela",
)

val browseCountries = listOf(
    BrowseCountry("italian", R.string.browse_italy, R.string.browse_italy_food, "🇮🇹", BrowseRegion.Europe, "it", R.drawable.flag_it),
    BrowseCountry("france", R.string.browse_france, R.string.browse_france_food, "🇫🇷", BrowseRegion.Europe, "fr", R.drawable.flag_fr),
    BrowseCountry("japanese", R.string.browse_japan, R.string.browse_japan_food, "🇯🇵", BrowseRegion.Asia, "jp", R.drawable.flag_jp),
    BrowseCountry("mexican", R.string.browse_mexico, R.string.browse_mexico_food, "🇲🇽", BrowseRegion.Americas, "mx", R.drawable.flag_mx),
    BrowseCountry("india", R.string.browse_india, R.string.browse_india_food, "🇮🇳", BrowseRegion.Asia, "in", R.drawable.flag_in),
    BrowseCountry("spanish", R.string.browse_spain, R.string.browse_spain_food, "🇪🇸", BrowseRegion.Europe, "es", R.drawable.flag_es),
    BrowseCountry("thai", R.string.browse_thailand, R.string.browse_thailand_food, "🇹🇭", BrowseRegion.Asia, "th", R.drawable.flag_th),
    BrowseCountry("moroccan", R.string.browse_morocco, R.string.browse_morocco_food, "🇲🇦", BrowseRegion.Africa, "ma", R.drawable.flag_ma),
    BrowseCountry("united states", R.string.browse_usa, R.string.browse_usa_food, "🇺🇸", BrowseRegion.Americas, "us", R.drawable.flag_us),
    BrowseCountry("british", R.string.browse_uk, R.string.browse_uk_food, "🇬🇧", BrowseRegion.Europe, "gb", R.drawable.flag_gb),
    BrowseCountry("canadian", R.string.browse_canada, R.string.browse_canada_food, "🇨🇦", BrowseRegion.Americas, "ca", R.drawable.flag_ca),
    BrowseCountry("jamaican", R.string.browse_jamaica, R.string.browse_jamaica_food, "🇯🇲", BrowseRegion.Americas, "jm", R.drawable.flag_jm),
    BrowseCountry("chinese", R.string.browse_china, R.string.browse_china_food, "🇨🇳", BrowseRegion.Asia, "cn", R.drawable.flag_cn),
    BrowseCountry("netherlands", R.string.browse_netherlands, R.string.browse_netherlands_food, "🇳🇱", BrowseRegion.Europe, "nl", R.drawable.flag_nl),
    BrowseCountry("egyptian", R.string.browse_egypt, R.string.browse_egypt_food, "🇪🇬", BrowseRegion.Africa, "eg", R.drawable.flag_eg),
    BrowseCountry("greek", R.string.browse_greece, R.string.browse_greece_food, "🇬🇷", BrowseRegion.Europe, "gr", R.drawable.flag_gr),
    BrowseCountry("irish", R.string.browse_ireland, R.string.browse_ireland_food, "🇮🇪", BrowseRegion.Europe, "ie", R.drawable.flag_ie),
    BrowseCountry("kenyan", R.string.browse_kenya, R.string.browse_kenya_food, "🇰🇪", BrowseRegion.Africa, "ke", R.drawable.flag_ke),
    BrowseCountry("malaysian", R.string.browse_malaysia, R.string.browse_malaysia_food, "🇲🇾", BrowseRegion.Asia, "my", R.drawable.flag_my),
    BrowseCountry("croatian", R.string.browse_croatia, R.string.browse_croatia_food, "🇭🇷", BrowseRegion.Europe, "hr", R.drawable.flag_hr),
    BrowseCountry("norway", R.string.browse_norway, R.string.browse_norway_food, "🇳🇴", BrowseRegion.Europe, "no", R.drawable.flag_no),
    BrowseCountry("portuguese", R.string.browse_portugal, R.string.browse_portugal_food, "🇵🇹", BrowseRegion.Europe, "pt", R.drawable.flag_pt),
    BrowseCountry("russian", R.string.browse_russia, R.string.browse_russia_food, "🇷🇺", BrowseRegion.Europe, "ru", R.drawable.flag_ru),
    BrowseCountry("argentina", R.string.browse_argentina, R.string.browse_argentina_food, "🇦🇷", BrowseRegion.Americas, "ar", R.drawable.flag_ar),
    BrowseCountry("slovakia", R.string.browse_slovakia, R.string.browse_slovakia_food, "🇸🇰", BrowseRegion.Europe, "sk", R.drawable.flag_sk),
    BrowseCountry("vietnamese", R.string.browse_vietnam, R.string.browse_vietnam_food, "🇻🇳", BrowseRegion.Asia, "vn", R.drawable.flag_vn),
    BrowseCountry("turkish", R.string.browse_turkey, R.string.browse_turkey_food, "🇹🇷", BrowseRegion.Asia, "tr", R.drawable.flag_tr),
    BrowseCountry("syrian", R.string.browse_syria, R.string.browse_syria_food, "🇸🇾", BrowseRegion.Asia, "sy", R.drawable.flag_sy),
    BrowseCountry("algerian", R.string.browse_algeria, R.string.browse_algeria_food, "🇩🇿", BrowseRegion.Africa, "dz", R.drawable.flag_dz),
    BrowseCountry("tunisian", R.string.browse_tunisia, R.string.browse_tunisia_food, "🇹🇳", BrowseRegion.Africa, "tn", R.drawable.flag_tn),
    BrowseCountry("polish", R.string.browse_poland, R.string.browse_poland_food, "🇵🇱", BrowseRegion.Europe, "pl", R.drawable.flag_pl),
    BrowseCountry("ukrainian", R.string.browse_ukraine, R.string.browse_ukraine_food, "🇺🇦", BrowseRegion.Europe, "ua", R.drawable.flag_ua),
    BrowseCountry("filipino", R.string.browse_philippines, R.string.browse_philippines_food, "🇵🇭", BrowseRegion.Asia, "ph", R.drawable.flag_ph),
    BrowseCountry("australian", R.string.browse_australia, R.string.browse_australia_food, "🇦🇺", BrowseRegion.Oceania, "au", R.drawable.flag_au),
    BrowseCountry("saudi arabian", R.string.browse_saudi_arabia, R.string.browse_saudi_arabia_food, "🇸🇦", BrowseRegion.Asia, "sa", R.drawable.flag_sa),
    BrowseCountry("laos", R.string.browse_laos, R.string.browse_laos_food, "🇱🇦", BrowseRegion.Asia, "la", R.drawable.flag_la),
    BrowseCountry("afghanistan", R.string.browse_afghanistan, R.string.browse_afghanistan_food, "🇦🇫", BrowseRegion.Asia, "af", R.drawable.flag_af),
    BrowseCountry("albania", R.string.browse_albania, R.string.browse_albania_food, "🇦🇱", BrowseRegion.Europe, "al", R.drawable.flag_al),
    BrowseCountry("andorra", R.string.browse_andorra, R.string.browse_andorra_food, "🇦🇩", BrowseRegion.Europe, "ad", R.drawable.flag_ad),
    BrowseCountry("angola", R.string.browse_angola, R.string.browse_angola_food, "🇦🇴", BrowseRegion.Africa, "ao", R.drawable.flag_ao),
    BrowseCountry("antigua and barbuda", R.string.browse_antigua_and_barbuda, R.string.browse_antigua_and_barbuda_food, "🇦🇬", BrowseRegion.Americas, "ag", R.drawable.flag_ag),
    BrowseCountry("armenia", R.string.browse_armenia, R.string.browse_armenia_food, "🇦🇲", BrowseRegion.Asia, "am", R.drawable.flag_am),
    BrowseCountry("aruba", R.string.browse_aruba, R.string.browse_aruba_food, "🇦🇼", BrowseRegion.Americas, "aw", R.drawable.flag_aw),
    BrowseCountry("austria", R.string.browse_austria, R.string.browse_austria_food, "🇦🇹", BrowseRegion.Europe, "at", R.drawable.flag_at),
    BrowseCountry("azerbaijan", R.string.browse_azerbaijan, R.string.browse_azerbaijan_food, "🇦🇿", BrowseRegion.Asia, "az", R.drawable.flag_az),
    BrowseCountry("bahamas", R.string.browse_bahamas, R.string.browse_bahamas_food, "🇧🇸", BrowseRegion.Americas, "bs", R.drawable.flag_bs),
    BrowseCountry("bahrain", R.string.browse_bahrain, R.string.browse_bahrain_food, "🇧🇭", BrowseRegion.Asia, "bh", R.drawable.flag_bh),
    BrowseCountry("bangladesh", R.string.browse_bangladesh, R.string.browse_bangladesh_food, "🇧🇩", BrowseRegion.Asia, "bd", R.drawable.flag_bd),
    BrowseCountry("barbados", R.string.browse_barbados, R.string.browse_barbados_food, "🇧🇧", BrowseRegion.Americas, "bb", R.drawable.flag_bb),
    BrowseCountry("belarus", R.string.browse_belarus, R.string.browse_belarus_food, "🇧🇾", BrowseRegion.Europe, "by", R.drawable.flag_by),
    BrowseCountry("belgium", R.string.browse_belgium, R.string.browse_belgium_food, "🇧🇪", BrowseRegion.Europe, "be", R.drawable.flag_be),
    BrowseCountry("belize", R.string.browse_belize, R.string.browse_belize_food, "🇧🇿", BrowseRegion.Americas, "bz", R.drawable.flag_bz),
    BrowseCountry("benin", R.string.browse_benin, R.string.browse_benin_food, "🇧🇯", BrowseRegion.Africa, "bj", R.drawable.flag_bj),
    BrowseCountry("bermuda", R.string.browse_bermuda, R.string.browse_bermuda_food, "🇧🇲", BrowseRegion.Americas, "bm", R.drawable.flag_bm),
    BrowseCountry("bhutan", R.string.browse_bhutan, R.string.browse_bhutan_food, "🇧🇹", BrowseRegion.Asia, "bt", R.drawable.flag_bt),
    BrowseCountry("bolivia", R.string.browse_bolivia, R.string.browse_bolivia_food, "🇧🇴", BrowseRegion.Americas, "bo", R.drawable.flag_bo),
    BrowseCountry("bosnia and herzegovina", R.string.browse_bosnia_and_herzegovina, R.string.browse_bosnia_and_herzegovina_food, "🇧🇦", BrowseRegion.Europe, "ba", R.drawable.flag_ba),
    BrowseCountry("botswana", R.string.browse_botswana, R.string.browse_botswana_food, "🇧🇼", BrowseRegion.Africa, "bw", R.drawable.flag_bw),
    BrowseCountry("brazil", R.string.browse_brazil, R.string.browse_brazil_food, "🇧🇷", BrowseRegion.Americas, "br", R.drawable.flag_br),
    BrowseCountry("brunei", R.string.browse_brunei, R.string.browse_brunei_food, "🇧🇳", BrowseRegion.Asia, "bn", R.drawable.flag_bn),
    BrowseCountry("bulgaria", R.string.browse_bulgaria, R.string.browse_bulgaria_food, "🇧🇬", BrowseRegion.Europe, "bg", R.drawable.flag_bg),
    BrowseCountry("burkina faso", R.string.browse_burkina_faso, R.string.browse_burkina_faso_food, "🇧🇫", BrowseRegion.Africa, "bf", R.drawable.flag_bf),
    BrowseCountry("burundi", R.string.browse_burundi, R.string.browse_burundi_food, "🇧🇮", BrowseRegion.Africa, "bi", R.drawable.flag_bi),
    BrowseCountry("cambodia", R.string.browse_cambodia, R.string.browse_cambodia_food, "🇰🇭", BrowseRegion.Asia, "kh", R.drawable.flag_kh),
    BrowseCountry("cameroon", R.string.browse_cameroon, R.string.browse_cameroon_food, "🇨🇲", BrowseRegion.Africa, "cm", R.drawable.flag_cm),
    BrowseCountry("cape verde", R.string.browse_cape_verde, R.string.browse_cape_verde_food, "🇨🇻", BrowseRegion.Africa, "cv", R.drawable.flag_cv),
    BrowseCountry("cayman islands", R.string.browse_cayman_islands, R.string.browse_cayman_islands_food, "🇰🇾", BrowseRegion.Americas, "ky", R.drawable.flag_ky),
    BrowseCountry("central african republic", R.string.browse_central_african_republic, R.string.browse_central_african_republic_food, "🇨🇫", BrowseRegion.Africa, "cf", R.drawable.flag_cf),
    BrowseCountry("chad", R.string.browse_chad, R.string.browse_chad_food, "🇹🇩", BrowseRegion.Africa, "td", R.drawable.flag_td),
    BrowseCountry("chile", R.string.browse_chile, R.string.browse_chile_food, "🇨🇱", BrowseRegion.Americas, "cl", R.drawable.flag_cl),
    BrowseCountry("colombia", R.string.browse_colombia, R.string.browse_colombia_food, "🇨🇴", BrowseRegion.Americas, "co", R.drawable.flag_co),
    BrowseCountry("costa rica", R.string.browse_costa_rica, R.string.browse_costa_rica_food, "🇨🇷", BrowseRegion.Americas, "cr", R.drawable.flag_cr),
    BrowseCountry("cuba", R.string.browse_cuba, R.string.browse_cuba_food, "🇨🇺", BrowseRegion.Americas, "cu", R.drawable.flag_cu),
    BrowseCountry("cyprus", R.string.browse_cyprus, R.string.browse_cyprus_food, "🇨🇾", BrowseRegion.Europe, "cy", R.drawable.flag_cy),
    BrowseCountry("czechia", R.string.browse_czechia, R.string.browse_czechia_food, "🇨🇿", BrowseRegion.Europe, "cz", R.drawable.flag_cz),
    BrowseCountry("denmark", R.string.browse_denmark, R.string.browse_denmark_food, "🇩🇰", BrowseRegion.Europe, "dk", R.drawable.flag_dk),
    BrowseCountry("djibouti", R.string.browse_djibouti, R.string.browse_djibouti_food, "🇩🇯", BrowseRegion.Africa, "dj", R.drawable.flag_dj),
    BrowseCountry("dominica", R.string.browse_dominica, R.string.browse_dominica_food, "🇩🇲", BrowseRegion.Americas, "dm", R.drawable.flag_dm),
    BrowseCountry("dominican republic", R.string.browse_dominican_republic, R.string.browse_dominican_republic_food, "🇩🇴", BrowseRegion.Americas, "do", R.drawable.flag_do),
    BrowseCountry("dr congo", R.string.browse_dr_congo, R.string.browse_dr_congo_food, "🇨🇩", BrowseRegion.Africa, "cd", R.drawable.flag_cd),
    BrowseCountry("ecuador", R.string.browse_ecuador, R.string.browse_ecuador_food, "🇪🇨", BrowseRegion.Americas, "ec", R.drawable.flag_ec),
    BrowseCountry("el salvador", R.string.browse_el_salvador, R.string.browse_el_salvador_food, "🇸🇻", BrowseRegion.Americas, "sv", R.drawable.flag_sv),
    BrowseCountry("equatorial guinea", R.string.browse_equatorial_guinea, R.string.browse_equatorial_guinea_food, "🇬🇶", BrowseRegion.Africa, "gq", R.drawable.flag_gq),
    BrowseCountry("eritrea", R.string.browse_eritrea, R.string.browse_eritrea_food, "🇪🇷", BrowseRegion.Africa, "er", R.drawable.flag_er),
    BrowseCountry("estonia", R.string.browse_estonia, R.string.browse_estonia_food, "🇪🇪", BrowseRegion.Europe, "ee", R.drawable.flag_ee),
    BrowseCountry("ethiopia", R.string.browse_ethiopia, R.string.browse_ethiopia_food, "🇪🇹", BrowseRegion.Africa, "et", R.drawable.flag_et),
    BrowseCountry("faroe islands", R.string.browse_faroe_islands, R.string.browse_faroe_islands_food, "🇫🇴", BrowseRegion.Europe, "fo", R.drawable.flag_fo),
    BrowseCountry("fiji", R.string.browse_fiji, R.string.browse_fiji_food, "🇫🇯", BrowseRegion.Oceania, "fj", R.drawable.flag_fj),
    BrowseCountry("finland", R.string.browse_finland, R.string.browse_finland_food, "🇫🇮", BrowseRegion.Europe, "fi", R.drawable.flag_fi),
    BrowseCountry("gabon", R.string.browse_gabon, R.string.browse_gabon_food, "🇬🇦", BrowseRegion.Africa, "ga", R.drawable.flag_ga),
    BrowseCountry("gambia", R.string.browse_gambia, R.string.browse_gambia_food, "🇬🇲", BrowseRegion.Africa, "gm", R.drawable.flag_gm),
    BrowseCountry("georgia", R.string.browse_georgia, R.string.browse_georgia_food, "🇬🇪", BrowseRegion.Asia, "ge", R.drawable.flag_ge),
    BrowseCountry("germany", R.string.browse_germany, R.string.browse_germany_food, "🇩🇪", BrowseRegion.Europe, "de", R.drawable.flag_de),
    BrowseCountry("ghana", R.string.browse_ghana, R.string.browse_ghana_food, "🇬🇭", BrowseRegion.Africa, "gh", R.drawable.flag_gh),
    BrowseCountry("gibraltar", R.string.browse_gibraltar, R.string.browse_gibraltar_food, "🇬🇮", BrowseRegion.Europe, "gi", R.drawable.flag_gi),
    BrowseCountry("greenland", R.string.browse_greenland, R.string.browse_greenland_food, "🇬🇱", BrowseRegion.Americas, "gl", R.drawable.flag_gl),
    BrowseCountry("grenada", R.string.browse_grenada, R.string.browse_grenada_food, "🇬🇩", BrowseRegion.Americas, "gd", R.drawable.flag_gd),
    BrowseCountry("guadeloupe", R.string.browse_guadeloupe, R.string.browse_guadeloupe_food, "🇬🇵", BrowseRegion.Americas, "gp", R.drawable.flag_gp),
    BrowseCountry("guam", R.string.browse_guam, R.string.browse_guam_food, "🇬🇺", BrowseRegion.Oceania, "gu", R.drawable.flag_gu),
    BrowseCountry("guatemala", R.string.browse_guatemala, R.string.browse_guatemala_food, "🇬🇹", BrowseRegion.Americas, "gt", R.drawable.flag_gt),
    BrowseCountry("guernsey", R.string.browse_guernsey, R.string.browse_guernsey_food, "🇬🇬", BrowseRegion.Europe, "gg", R.drawable.flag_gg),
    BrowseCountry("guinea", R.string.browse_guinea, R.string.browse_guinea_food, "🇬🇳", BrowseRegion.Africa, "gn", R.drawable.flag_gn),
    BrowseCountry("guinea-bissau", R.string.browse_guinea_bissau, R.string.browse_guinea_bissau_food, "🇬🇼", BrowseRegion.Africa, "gw", R.drawable.flag_gw),
    BrowseCountry("guyana", R.string.browse_guyana, R.string.browse_guyana_food, "🇬🇾", BrowseRegion.Americas, "gy", R.drawable.flag_gy),
    BrowseCountry("haiti", R.string.browse_haiti, R.string.browse_haiti_food, "🇭🇹", BrowseRegion.Americas, "ht", R.drawable.flag_ht),
    BrowseCountry("honduras", R.string.browse_honduras, R.string.browse_honduras_food, "🇭🇳", BrowseRegion.Americas, "hn", R.drawable.flag_hn),
    BrowseCountry("hong kong", R.string.browse_hong_kong, R.string.browse_hong_kong_food, "🇭🇰", BrowseRegion.Asia, "hk", R.drawable.flag_hk),
    BrowseCountry("hungary", R.string.browse_hungary, R.string.browse_hungary_food, "🇭🇺", BrowseRegion.Europe, "hu", R.drawable.flag_hu),
    BrowseCountry("iceland", R.string.browse_iceland, R.string.browse_iceland_food, "🇮🇸", BrowseRegion.Europe, "is", R.drawable.flag_is),
    BrowseCountry("indonesia", R.string.browse_indonesia, R.string.browse_indonesia_food, "🇮🇩", BrowseRegion.Asia, "id", R.drawable.flag_id),
    BrowseCountry("iran", R.string.browse_iran, R.string.browse_iran_food, "🇮🇷", BrowseRegion.Asia, "ir", R.drawable.flag_ir),
    BrowseCountry("iraq", R.string.browse_iraq, R.string.browse_iraq_food, "🇮🇶", BrowseRegion.Asia, "iq", R.drawable.flag_iq),
    BrowseCountry("israel", R.string.browse_israel, R.string.browse_israel_food, "🇮🇱", BrowseRegion.Asia, "il", R.drawable.flag_il),
    BrowseCountry("ivory coast", R.string.browse_ivory_coast, R.string.browse_ivory_coast_food, "🇨🇮", BrowseRegion.Africa, "ci", R.drawable.flag_ci),
    BrowseCountry("jersey", R.string.browse_jersey, R.string.browse_jersey_food, "🇯🇪", BrowseRegion.Europe, "je", R.drawable.flag_je),
    BrowseCountry("jordan", R.string.browse_jordan, R.string.browse_jordan_food, "🇯🇴", BrowseRegion.Asia, "jo", R.drawable.flag_jo),
    BrowseCountry("kazakhstan", R.string.browse_kazakhstan, R.string.browse_kazakhstan_food, "🇰🇿", BrowseRegion.Asia, "kz", R.drawable.flag_kz),
    BrowseCountry("kosovo", R.string.browse_kosovo, R.string.browse_kosovo_food, "🇽🇰", BrowseRegion.Europe, "xk", R.drawable.flag_xk),
    BrowseCountry("kuwait", R.string.browse_kuwait, R.string.browse_kuwait_food, "🇰🇼", BrowseRegion.Asia, "kw", R.drawable.flag_kw),
    BrowseCountry("kyrgyzstan", R.string.browse_kyrgyzstan, R.string.browse_kyrgyzstan_food, "🇰🇬", BrowseRegion.Asia, "kg", R.drawable.flag_kg),
    BrowseCountry("latvia", R.string.browse_latvia, R.string.browse_latvia_food, "🇱🇻", BrowseRegion.Europe, "lv", R.drawable.flag_lv),
    BrowseCountry("lebanon", R.string.browse_lebanon, R.string.browse_lebanon_food, "🇱🇧", BrowseRegion.Asia, "lb", R.drawable.flag_lb),
    BrowseCountry("lesotho", R.string.browse_lesotho, R.string.browse_lesotho_food, "🇱🇸", BrowseRegion.Africa, "ls", R.drawable.flag_ls),
    BrowseCountry("liberia", R.string.browse_liberia, R.string.browse_liberia_food, "🇱🇷", BrowseRegion.Africa, "lr", R.drawable.flag_lr),
    BrowseCountry("libya", R.string.browse_libya, R.string.browse_libya_food, "🇱🇾", BrowseRegion.Africa, "ly", R.drawable.flag_ly),
    BrowseCountry("liechtenstein", R.string.browse_liechtenstein, R.string.browse_liechtenstein_food, "🇱🇮", BrowseRegion.Europe, "li", R.drawable.flag_li),
    BrowseCountry("lithuania", R.string.browse_lithuania, R.string.browse_lithuania_food, "🇱🇹", BrowseRegion.Europe, "lt", R.drawable.flag_lt),
    BrowseCountry("luxembourg", R.string.browse_luxembourg, R.string.browse_luxembourg_food, "🇱🇺", BrowseRegion.Europe, "lu", R.drawable.flag_lu),
    BrowseCountry("madagascar", R.string.browse_madagascar, R.string.browse_madagascar_food, "🇲🇬", BrowseRegion.Africa, "mg", R.drawable.flag_mg),
    BrowseCountry("malawi", R.string.browse_malawi, R.string.browse_malawi_food, "🇲🇼", BrowseRegion.Africa, "mw", R.drawable.flag_mw),
    BrowseCountry("maldives", R.string.browse_maldives, R.string.browse_maldives_food, "🇲🇻", BrowseRegion.Asia, "mv", R.drawable.flag_mv),
    BrowseCountry("mali", R.string.browse_mali, R.string.browse_mali_food, "🇲🇱", BrowseRegion.Africa, "ml", R.drawable.flag_ml),
    BrowseCountry("malta", R.string.browse_malta, R.string.browse_malta_food, "🇲🇹", BrowseRegion.Europe, "mt", R.drawable.flag_mt),
    BrowseCountry("mauritius", R.string.browse_mauritius, R.string.browse_mauritius_food, "🇲🇺", BrowseRegion.Africa, "mu", R.drawable.flag_mu),
    BrowseCountry("moldova", R.string.browse_moldova, R.string.browse_moldova_food, "🇲🇩", BrowseRegion.Europe, "md", R.drawable.flag_md),
    BrowseCountry("mongolia", R.string.browse_mongolia, R.string.browse_mongolia_food, "🇲🇳", BrowseRegion.Asia, "mn", R.drawable.flag_mn),
    BrowseCountry("montenegro", R.string.browse_montenegro, R.string.browse_montenegro_food, "🇲🇪", BrowseRegion.Europe, "me", R.drawable.flag_me),
    BrowseCountry("mozambique", R.string.browse_mozambique, R.string.browse_mozambique_food, "🇲🇿", BrowseRegion.Africa, "mz", R.drawable.flag_mz),
    BrowseCountry("myanmar", R.string.browse_myanmar, R.string.browse_myanmar_food, "🇲🇲", BrowseRegion.Asia, "mm", R.drawable.flag_mm),
    BrowseCountry("namibia", R.string.browse_namibia, R.string.browse_namibia_food, "🇳🇦", BrowseRegion.Africa, "na", R.drawable.flag_na),
    BrowseCountry("nepal", R.string.browse_nepal, R.string.browse_nepal_food, "🇳🇵", BrowseRegion.Asia, "np", R.drawable.flag_np),
    BrowseCountry("new zealand", R.string.browse_new_zealand, R.string.browse_new_zealand_food, "🇳🇿", BrowseRegion.Oceania, "nz", R.drawable.flag_nz),
    BrowseCountry("nicaragua", R.string.browse_nicaragua, R.string.browse_nicaragua_food, "🇳🇮", BrowseRegion.Americas, "ni", R.drawable.flag_ni),
    BrowseCountry("niger", R.string.browse_niger, R.string.browse_niger_food, "🇳🇪", BrowseRegion.Africa, "ne", R.drawable.flag_ne),
    BrowseCountry("nigeria", R.string.browse_nigeria, R.string.browse_nigeria_food, "🇳🇬", BrowseRegion.Africa, "ng", R.drawable.flag_ng),
    BrowseCountry("north korea", R.string.browse_north_korea, R.string.browse_north_korea_food, "🇰🇵", BrowseRegion.Asia, "kp", R.drawable.flag_kp),
    BrowseCountry("north macedonia", R.string.browse_north_macedonia, R.string.browse_north_macedonia_food, "🇲🇰", BrowseRegion.Europe, "mk", R.drawable.flag_mk),
    BrowseCountry("oman", R.string.browse_oman, R.string.browse_oman_food, "🇴🇲", BrowseRegion.Asia, "om", R.drawable.flag_om),
    BrowseCountry("pakistan", R.string.browse_pakistan, R.string.browse_pakistan_food, "🇵🇰", BrowseRegion.Asia, "pk", R.drawable.flag_pk),
    BrowseCountry("palestine", R.string.browse_palestine, R.string.browse_palestine_food, "🇵🇸", BrowseRegion.Asia, "ps", R.drawable.flag_ps),
    BrowseCountry("panama", R.string.browse_panama, R.string.browse_panama_food, "🇵🇦", BrowseRegion.Americas, "pa", R.drawable.flag_pa),
    BrowseCountry("papua new guinea", R.string.browse_papua_new_guinea, R.string.browse_papua_new_guinea_food, "🇵🇬", BrowseRegion.Oceania, "pg", R.drawable.flag_pg),
    BrowseCountry("paraguay", R.string.browse_paraguay, R.string.browse_paraguay_food, "🇵🇾", BrowseRegion.Americas, "py", R.drawable.flag_py),
    BrowseCountry("peru", R.string.browse_peru, R.string.browse_peru_food, "🇵🇪", BrowseRegion.Americas, "pe", R.drawable.flag_pe),
    BrowseCountry("puerto rico", R.string.browse_puerto_rico, R.string.browse_puerto_rico_food, "🇵🇷", BrowseRegion.Americas, "pr", R.drawable.flag_pr),
    BrowseCountry("qatar", R.string.browse_qatar, R.string.browse_qatar_food, "🇶🇦", BrowseRegion.Asia, "qa", R.drawable.flag_qa),
    BrowseCountry("republic of the congo", R.string.browse_republic_of_the_congo, R.string.browse_republic_of_the_congo_food, "🇨🇬", BrowseRegion.Africa, "cg", R.drawable.flag_cg),
    BrowseCountry("romania", R.string.browse_romania, R.string.browse_romania_food, "🇷🇴", BrowseRegion.Europe, "ro", R.drawable.flag_ro),
    BrowseCountry("rwanda", R.string.browse_rwanda, R.string.browse_rwanda_food, "🇷🇼", BrowseRegion.Africa, "rw", R.drawable.flag_rw),
    BrowseCountry("saint lucia", R.string.browse_saint_lucia, R.string.browse_saint_lucia_food, "🇱🇨", BrowseRegion.Americas, "lc", R.drawable.flag_lc),
    BrowseCountry("samoa", R.string.browse_samoa, R.string.browse_samoa_food, "🇼🇸", BrowseRegion.Oceania, "ws", R.drawable.flag_ws),
    BrowseCountry("san marino", R.string.browse_san_marino, R.string.browse_san_marino_food, "🇸🇲", BrowseRegion.Europe, "sm", R.drawable.flag_sm),
    BrowseCountry("senegal", R.string.browse_senegal, R.string.browse_senegal_food, "🇸🇳", BrowseRegion.Africa, "sn", R.drawable.flag_sn),
    BrowseCountry("serbia", R.string.browse_serbia, R.string.browse_serbia_food, "🇷🇸", BrowseRegion.Europe, "rs", R.drawable.flag_rs),
    BrowseCountry("seychelles", R.string.browse_seychelles, R.string.browse_seychelles_food, "🇸🇨", BrowseRegion.Africa, "sc", R.drawable.flag_sc),
    BrowseCountry("sierra leone", R.string.browse_sierra_leone, R.string.browse_sierra_leone_food, "🇸🇱", BrowseRegion.Africa, "sl", R.drawable.flag_sl),
    BrowseCountry("singapore", R.string.browse_singapore, R.string.browse_singapore_food, "🇸🇬", BrowseRegion.Asia, "sg", R.drawable.flag_sg),
    BrowseCountry("slovenia", R.string.browse_slovenia, R.string.browse_slovenia_food, "🇸🇮", BrowseRegion.Europe, "si", R.drawable.flag_si),
    BrowseCountry("solomon islands", R.string.browse_solomon_islands, R.string.browse_solomon_islands_food, "🇸🇧", BrowseRegion.Oceania, "sb", R.drawable.flag_sb),
    BrowseCountry("somalia", R.string.browse_somalia, R.string.browse_somalia_food, "🇸🇴", BrowseRegion.Africa, "so", R.drawable.flag_so),
    BrowseCountry("south africa", R.string.browse_south_africa, R.string.browse_south_africa_food, "🇿🇦", BrowseRegion.Africa, "za", R.drawable.flag_za),
    BrowseCountry("south korea", R.string.browse_south_korea, R.string.browse_south_korea_food, "🇰🇷", BrowseRegion.Asia, "kr", R.drawable.flag_kr),
    BrowseCountry("south sudan", R.string.browse_south_sudan, R.string.browse_south_sudan_food, "🇸🇸", BrowseRegion.Africa, "ss", R.drawable.flag_ss),
    BrowseCountry("sri lanka", R.string.browse_sri_lanka, R.string.browse_sri_lanka_food, "🇱🇰", BrowseRegion.Asia, "lk", R.drawable.flag_lk),
    BrowseCountry("sudan", R.string.browse_sudan, R.string.browse_sudan_food, "🇸🇩", BrowseRegion.Africa, "sd", R.drawable.flag_sd),
    BrowseCountry("suriname", R.string.browse_suriname, R.string.browse_suriname_food, "🇸🇷", BrowseRegion.Americas, "sr", R.drawable.flag_sr),
    BrowseCountry("sweden", R.string.browse_sweden, R.string.browse_sweden_food, "🇸🇪", BrowseRegion.Europe, "se", R.drawable.flag_se),
    BrowseCountry("switzerland", R.string.browse_switzerland, R.string.browse_switzerland_food, "🇨🇭", BrowseRegion.Europe, "ch", R.drawable.flag_ch),
    BrowseCountry("taiwan", R.string.browse_taiwan, R.string.browse_taiwan_food, "🇹🇼", BrowseRegion.Asia, "tw", R.drawable.flag_tw),
    BrowseCountry("tajikistan", R.string.browse_tajikistan, R.string.browse_tajikistan_food, "🇹🇯", BrowseRegion.Asia, "tj", R.drawable.flag_tj),
    BrowseCountry("tanzania", R.string.browse_tanzania, R.string.browse_tanzania_food, "🇹🇿", BrowseRegion.Africa, "tz", R.drawable.flag_tz),
    BrowseCountry("togo", R.string.browse_togo, R.string.browse_togo_food, "🇹🇬", BrowseRegion.Africa, "tg", R.drawable.flag_tg),
    BrowseCountry("tonga", R.string.browse_tonga, R.string.browse_tonga_food, "🇹🇴", BrowseRegion.Oceania, "to", R.drawable.flag_to),
    BrowseCountry("trinidad and tobago", R.string.browse_trinidad_and_tobago, R.string.browse_trinidad_and_tobago_food, "🇹🇹", BrowseRegion.Americas, "tt", R.drawable.flag_tt),
    BrowseCountry("turkmenistan", R.string.browse_turkmenistan, R.string.browse_turkmenistan_food, "🇹🇲", BrowseRegion.Asia, "tm", R.drawable.flag_tm),
    BrowseCountry("tuvalu", R.string.browse_tuvalu, R.string.browse_tuvalu_food, "🇹🇻", BrowseRegion.Oceania, "tv", R.drawable.flag_tv),
    BrowseCountry("uganda", R.string.browse_uganda, R.string.browse_uganda_food, "🇺🇬", BrowseRegion.Africa, "ug", R.drawable.flag_ug),
    BrowseCountry("united arab emirates", R.string.browse_united_arab_emirates, R.string.browse_united_arab_emirates_food, "🇦🇪", BrowseRegion.Asia, "ae", R.drawable.flag_ae),
    BrowseCountry("uruguay", R.string.browse_uruguay, R.string.browse_uruguay_food, "🇺🇾", BrowseRegion.Americas, "uy", R.drawable.flag_uy),
    BrowseCountry("uzbekistan", R.string.browse_uzbekistan, R.string.browse_uzbekistan_food, "🇺🇿", BrowseRegion.Asia, "uz", R.drawable.flag_uz),
    BrowseCountry("vanuatu", R.string.browse_vanuatu, R.string.browse_vanuatu_food, "🇻🇺", BrowseRegion.Oceania, "vu", R.drawable.flag_vu),
    BrowseCountry("venezuela", R.string.browse_venezuela, R.string.browse_venezuela_food, "🇻🇪", BrowseRegion.Americas, "ve", R.drawable.flag_ve),
    BrowseCountry("yemen", R.string.browse_yemen, R.string.browse_yemen_food, "🇾🇪", BrowseRegion.Asia, "ye", R.drawable.flag_ye),
    BrowseCountry("zambia", R.string.browse_zambia, R.string.browse_zambia_food, "🇿🇲", BrowseRegion.Africa, "zm", R.drawable.flag_zm),
    BrowseCountry("zimbabwe", R.string.browse_zimbabwe, R.string.browse_zimbabwe_food, "🇿🇼", BrowseRegion.Africa, "zw", R.drawable.flag_zw),
).filter { it.apiArea in supportedCountryAreas }

fun findBrowseCountry(value: String): BrowseCountry? {
    val route = value.trim()
    browseCountries.firstOrNull { it.apiArea.equals(route, ignoreCase = true) }?.let { return it }
    val flag = countryFlagResource(route)
    return if (flag == R.drawable.unknown) null else browseCountries.singleOrNull { it.flagResource == flag }
}

fun filterCountries(region: BrowseRegion): List<BrowseCountry> =
    browseCountries.filter { region == BrowseRegion.All || it.region == region }

@DrawableRes
fun countryFlagResource(apiArea: String): Int = when (apiArea.trim().lowercase()) {
    "italian", "italy" -> R.drawable.flag_it
    "french", "france" -> R.drawable.flag_fr
    "japanese", "japan" -> R.drawable.flag_jp
    "mexican", "mexico" -> R.drawable.flag_mx
    "indian", "india" -> R.drawable.flag_in
    "spanish", "spain" -> R.drawable.flag_es
    "thai", "thailand" -> R.drawable.flag_th
    "moroccan", "morocco" -> R.drawable.flag_ma
    "american", "united states" -> R.drawable.flag_us
    "british", "united kingdom" -> R.drawable.flag_gb
    "canadian", "canada" -> R.drawable.flag_ca
    "jamaican", "jamaica" -> R.drawable.flag_jm
    "chinese", "china" -> R.drawable.flag_cn
    "dutch", "netherlands" -> R.drawable.flag_nl
    "egyptian", "egypt" -> R.drawable.flag_eg
    "greek", "greece" -> R.drawable.flag_gr
    "irish", "ireland" -> R.drawable.flag_ie
    "kenyan", "kenya" -> R.drawable.flag_ke
    "malaysian", "malaysia" -> R.drawable.flag_my
    "croatian", "croatia" -> R.drawable.flag_hr
    "norwegian", "norway" -> R.drawable.flag_no
    "portuguese", "portugal" -> R.drawable.flag_pt
    "russian", "russia" -> R.drawable.flag_ru
    "argentinian", "argentine", "argentina" -> R.drawable.flag_ar
    "slovakian", "slovak", "slovakia" -> R.drawable.flag_sk
    "vietnamese", "vietnam" -> R.drawable.flag_vn
    "turkish", "turkey" -> R.drawable.flag_tr
    "syrian", "syria" -> R.drawable.flag_sy
    "algerian", "algeria" -> R.drawable.flag_dz
    "tunisian", "tunisia" -> R.drawable.flag_tn
    "polish", "poland" -> R.drawable.flag_pl
    "ukrainian", "ukraine" -> R.drawable.flag_ua
    "filipino", "philippines" -> R.drawable.flag_ph
    "australian", "australia" -> R.drawable.flag_au
    "saudi arabian", "saudi arabia" -> R.drawable.flag_sa
    "laotian", "laos" -> R.drawable.flag_la
    "afghan", "afghanistan" -> R.drawable.flag_af
    "albanian", "albania" -> R.drawable.flag_al
    "andorran", "andorra" -> R.drawable.flag_ad
    "angolan", "angola" -> R.drawable.flag_ao
    "antiguan, barbudan", "antigua and barbuda" -> R.drawable.flag_ag
    "armenian", "armenia" -> R.drawable.flag_am
    "aruban", "aruba" -> R.drawable.flag_aw
    "austrian", "austria" -> R.drawable.flag_at
    "azerbaijani", "azerbaijan" -> R.drawable.flag_az
    "bahamian", "bahamas" -> R.drawable.flag_bs
    "bahraini", "bahrain" -> R.drawable.flag_bh
    "bangladeshi", "bangladesh" -> R.drawable.flag_bd
    "barbadian", "barbados" -> R.drawable.flag_bb
    "belarusian", "belarus" -> R.drawable.flag_by
    "belgian", "belgium" -> R.drawable.flag_be
    "belizean", "belize" -> R.drawable.flag_bz
    "beninese", "benin" -> R.drawable.flag_bj
    "bermudian", "bermuda" -> R.drawable.flag_bm
    "bhutanese", "bhutan" -> R.drawable.flag_bt
    "bolivian", "bolivia" -> R.drawable.flag_bo
    "bosnian, herzegovinian", "bosnia and herzegovina" -> R.drawable.flag_ba
    "motswana", "botswana" -> R.drawable.flag_bw
    "brazilian", "brazil" -> R.drawable.flag_br
    "bruneian", "brunei" -> R.drawable.flag_bn
    "bulgarian", "bulgaria" -> R.drawable.flag_bg
    "burkinabe", "burkina faso" -> R.drawable.flag_bf
    "burundian", "burundi" -> R.drawable.flag_bi
    "cambodian", "cambodia" -> R.drawable.flag_kh
    "cameroonian", "cameroon" -> R.drawable.flag_cm
    "cape verdian", "cape verde" -> R.drawable.flag_cv
    "caymanian", "cayman islands" -> R.drawable.flag_ky
    "central african", "central african republic" -> R.drawable.flag_cf
    "chadian", "chad" -> R.drawable.flag_td
    "chilean", "chile" -> R.drawable.flag_cl
    "colombian", "colombia" -> R.drawable.flag_co
    "costa rican", "costa rica" -> R.drawable.flag_cr
    "cuban", "cuba" -> R.drawable.flag_cu
    "cypriot", "cyprus" -> R.drawable.flag_cy
    "czech", "czechia" -> R.drawable.flag_cz
    "danish", "denmark" -> R.drawable.flag_dk
    "djibouti" -> R.drawable.flag_dj
    "ecuadorean", "ecuador" -> R.drawable.flag_ec
    "salvadoran", "el salvador" -> R.drawable.flag_sv
    "equatorial guinean", "equatorial guinea" -> R.drawable.flag_gq
    "eritrean", "eritrea" -> R.drawable.flag_er
    "estonian", "estonia" -> R.drawable.flag_ee
    "ethiopian", "ethiopia" -> R.drawable.flag_et
    "faroese", "faroe islands" -> R.drawable.flag_fo
    "fijian", "fiji" -> R.drawable.flag_fj
    "finnish", "finland" -> R.drawable.flag_fi
    "gabonese", "gabon" -> R.drawable.flag_ga
    "gambian", "gambia" -> R.drawable.flag_gm
    "georgian", "georgia" -> R.drawable.flag_ge
    "german", "germany" -> R.drawable.flag_de
    "ghanaian", "ghana" -> R.drawable.flag_gh
    "gibraltar" -> R.drawable.flag_gi
    "greenlandic", "greenland" -> R.drawable.flag_gl
    "grenadian", "grenada" -> R.drawable.flag_gd
    "guadeloupian", "guadeloupe" -> R.drawable.flag_gp
    "guamanian", "guam" -> R.drawable.flag_gu
    "guatemalan", "guatemala" -> R.drawable.flag_gt
    "guinean", "guinea" -> R.drawable.flag_gn
    "guinea-bissauan", "guinea-bissau" -> R.drawable.flag_gw
    "guyanese", "guyana" -> R.drawable.flag_gy
    "haitian", "haiti" -> R.drawable.flag_ht
    "honduran", "honduras" -> R.drawable.flag_hn
    "hong konger", "hong kong" -> R.drawable.flag_hk
    "hungarian", "hungary" -> R.drawable.flag_hu
    "icelander", "iceland" -> R.drawable.flag_is
    "indonesian", "indonesia" -> R.drawable.flag_id
    "iranian", "iran" -> R.drawable.flag_ir
    "iraqi", "iraq" -> R.drawable.flag_iq
    "israeli", "israel" -> R.drawable.flag_il
    "ivorian", "ivory coast" -> R.drawable.flag_ci
    "jordanian", "jordan" -> R.drawable.flag_jo
    "kazakhstani", "kazakhstan" -> R.drawable.flag_kz
    "kosovar", "kosovo" -> R.drawable.flag_xk
    "kuwaiti", "kuwait" -> R.drawable.flag_kw
    "kirghiz", "kyrgyzstan" -> R.drawable.flag_kg
    "latvian", "latvia" -> R.drawable.flag_lv
    "lebanese", "lebanon" -> R.drawable.flag_lb
    "mosotho", "lesotho" -> R.drawable.flag_ls
    "liberian", "liberia" -> R.drawable.flag_lr
    "libyan", "libya" -> R.drawable.flag_ly
    "liechtensteiner", "liechtenstein" -> R.drawable.flag_li
    "lithuanian", "lithuania" -> R.drawable.flag_lt
    "luxembourger", "luxembourg" -> R.drawable.flag_lu
    "malagasy", "madagascar" -> R.drawable.flag_mg
    "malawian", "malawi" -> R.drawable.flag_mw
    "maldivan", "maldives" -> R.drawable.flag_mv
    "malian", "mali" -> R.drawable.flag_ml
    "maltese", "malta" -> R.drawable.flag_mt
    "mauritian", "mauritius" -> R.drawable.flag_mu
    "moldovan", "moldova" -> R.drawable.flag_md
    "mongolian", "mongolia" -> R.drawable.flag_mn
    "montenegrin", "montenegro" -> R.drawable.flag_me
    "mozambican", "mozambique" -> R.drawable.flag_mz
    "burmese", "myanmar" -> R.drawable.flag_mm
    "namibian", "namibia" -> R.drawable.flag_na
    "nepalese", "nepal" -> R.drawable.flag_np
    "new zealander", "new zealand" -> R.drawable.flag_nz
    "nicaraguan", "nicaragua" -> R.drawable.flag_ni
    "nigerien", "niger" -> R.drawable.flag_ne
    "nigerian", "nigeria" -> R.drawable.flag_ng
    "north korean", "north korea" -> R.drawable.flag_kp
    "macedonian", "north macedonia" -> R.drawable.flag_mk
    "omani", "oman" -> R.drawable.flag_om
    "pakistani", "pakistan" -> R.drawable.flag_pk
    "palestinian", "palestine" -> R.drawable.flag_ps
    "panamanian", "panama" -> R.drawable.flag_pa
    "papua new guinean", "papua new guinea" -> R.drawable.flag_pg
    "paraguayan", "paraguay" -> R.drawable.flag_py
    "peruvian", "peru" -> R.drawable.flag_pe
    "puerto rican", "puerto rico" -> R.drawable.flag_pr
    "qatari", "qatar" -> R.drawable.flag_qa
    "romanian", "romania" -> R.drawable.flag_ro
    "rwandan", "rwanda" -> R.drawable.flag_rw
    "saint lucian", "saint lucia" -> R.drawable.flag_lc
    "samoan", "samoa" -> R.drawable.flag_ws
    "sammarinese", "san marino" -> R.drawable.flag_sm
    "senegalese", "senegal" -> R.drawable.flag_sn
    "serbian", "serbia" -> R.drawable.flag_rs
    "seychellois", "seychelles" -> R.drawable.flag_sc
    "sierra leonean", "sierra leone" -> R.drawable.flag_sl
    "singaporean", "singapore" -> R.drawable.flag_sg
    "slovene", "slovenia" -> R.drawable.flag_si
    "solomon islander", "solomon islands" -> R.drawable.flag_sb
    "somali", "somalia" -> R.drawable.flag_so
    "south african", "south africa" -> R.drawable.flag_za
    "south korean", "south korea" -> R.drawable.flag_kr
    "south sudanese", "south sudan" -> R.drawable.flag_ss
    "sri lankan", "sri lanka" -> R.drawable.flag_lk
    "sudanese", "sudan" -> R.drawable.flag_sd
    "surinamer", "suriname" -> R.drawable.flag_sr
    "swedish", "sweden" -> R.drawable.flag_se
    "swiss", "switzerland" -> R.drawable.flag_ch
    "taiwanese", "taiwan" -> R.drawable.flag_tw
    "tadzhik", "tajikistan" -> R.drawable.flag_tj
    "tanzanian", "tanzania" -> R.drawable.flag_tz
    "togolese", "togo" -> R.drawable.flag_tg
    "tongan", "tonga" -> R.drawable.flag_to
    "trinidadian", "trinidad and tobago" -> R.drawable.flag_tt
    "turkmen", "turkmenistan" -> R.drawable.flag_tm
    "tuvaluan", "tuvalu" -> R.drawable.flag_tv
    "ugandan", "uganda" -> R.drawable.flag_ug
    "emirati", "united arab emirates" -> R.drawable.flag_ae
    "uruguayan", "uruguay" -> R.drawable.flag_uy
    "uzbekistani", "uzbekistan" -> R.drawable.flag_uz
    "ni-vanuatu", "vanuatu" -> R.drawable.flag_vu
    "venezuelan", "venezuela" -> R.drawable.flag_ve
    "yemeni", "yemen" -> R.drawable.flag_ye
    "zambian", "zambia" -> R.drawable.flag_zm
    "zimbabwean", "zimbabwe" -> R.drawable.flag_zw
    "dominica" -> R.drawable.flag_dm
    "dominican republic" -> R.drawable.flag_do
    "dr congo" -> R.drawable.flag_cd
    "guernsey" -> R.drawable.flag_gg
    "jersey" -> R.drawable.flag_je
    "republic of the congo" -> R.drawable.flag_cg
    // Legacy shared adjectives cannot identify a country-specific flag.
    "dominican", "channel islander", "congolese" -> R.drawable.unknown
    else -> R.drawable.unknown
}

fun browseColumnCount(widthDp: Float, fontScale: Float): Int {
    val available = (widthDp.coerceAtMost(KitchenLayout.contentMaxWidth.value) -
            KitchenSpacing.screen.value * 2).coerceAtLeast(0f)
    val minimumCardWidth = KitchenSizes.countryCardMinWidth.value * fontScale.coerceAtLeast(1f)
    val gap = KitchenSpacing.md.value
    val maxColumns = if (widthDp < KitchenLayout.mediumBreakpoint.value) 2 else 4
    return floor((available + gap) / (minimumCardWidth + gap)).toInt().coerceIn(1, maxColumns)
}
