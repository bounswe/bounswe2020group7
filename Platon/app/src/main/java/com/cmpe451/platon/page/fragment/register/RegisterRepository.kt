package com.cmpe451.platon.page.fragment.register

import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.RetrofitClient
import com.cmpe451.platon.network.models.Job
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterRepository(){

    var registerResourceResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    var jobListResourceResponse:MutableLiveData<Resource<List<Job>>> = MutableLiveData()

    fun postRegister(firstName:String, lastName:String, email:String,  password:String, job:String, institution:String?){
        val service = RetrofitClient.getService()

        val call = service.makeRegister(firstName, lastName,email, password, job, institution)

        registerResourceResponse.value = Resource.Loading()

        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful -> registerResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> registerResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                     else ->registerResourceResponse.value =  Resource.Error("Unknown error")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun getAllJobs() {
        val service = RetrofitClient.getService()
        val call = service.getAllJobs()

        jobListResourceResponse.value = Resource.Loading()

        call.enqueue(object : Callback<List<Job>?> {
            override fun onResponse(call: Call<List<Job>?>, response: Response<List<Job>?>) {
                when {
                    response.isSuccessful -> jobListResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> jobListResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else ->jobListResourceResponse.value =  Resource.Error("Unknown error")
                }
            }

            override fun onFailure(call: Call<List<Job>?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }


    val terms = "Welcome to platon!\n" +
            "\n" +
            "These terms and conditions outline the rules and regulations for the use of Platon's Website, located at platon.com.\n" +
            "\n" +
            "By accessing this website we assume you accept these terms and conditions. Do not continue to use platon if you do not agree to take all of the terms and conditions stated on this page.\n" +
            "\n" +
            "The following terminology applies to these Terms and Conditions, Privacy Statement and Disclaimer Notice and all Agreements: \"Client\", \"You\" and \"Your\" refers to you, the person log on this website and compliant to the Company’s terms and conditions. \"The Company\", \"Ourselves\", \"We\", \"Our\" and \"Us\", refers to our Company. \"Party\", \"Parties\", or \"Us\", refers to both the Client and ourselves. All terms refer to the offer, acceptance and consideration of payment necessary to undertake the process of our assistance to the Client in the most appropriate manner for the express purpose of meeting the Client’s needs in respect of provision of the Company’s stated services, in accordance with and subject to, prevailing law of Netherlands. Any use of the above terminology or other words in the singular, plural, capitalization and/or he/she or they, are taken as interchangeable and therefore as referring to same.\n" +
            "\n" +
            "Cookies\n" +
            "We employ the use of cookies. By accessing platon, you agreed to use cookies in agreement with the Platon's Privacy Policy.\n" +
            "\n" +
            "Most interactive websites use cookies to let us retrieve the user’s details for each visit. Cookies are used by our website to enable the functionality of certain areas to make it easier for people visiting our website. Some of our affiliate/advertising partners may also use cookies.\n" +
            "\n" +
            "License\n" +
            "Unless otherwise stated, Platon and/or its licensors own the intellectual property rights for all material on platon. All intellectual property rights are reserved. You may access this from platon for your own personal use subjected to restrictions set in these terms and conditions.\n" +
            "\n" +
            "You must not:\n" +
            "\n" +
            "Republish material from platon\n" +
            "Sell, rent or sub-license material from platon\n" +
            "Reproduce, duplicate or copy material from platon\n" +
            "Redistribute content from platon\n" +
            "This Agreement shall begin on the date hereof. Our Terms and Conditions were created with the help of the Terms And Conditions Generator and the Privacy Policy Generator.\n" +
            "\n" +
            "Parts of this website offer an opportunity for users to post and exchange opinions and information in certain areas of the website. Platon does not filter, edit, publish or review Comments prior to their presence on the website. Comments do not reflect the views and opinions of Platon,its agents and/or affiliates. Comments reflect the views and opinions of the person who post their views and opinions. To the extent permitted by applicable laws, Platon shall not be liable for the Comments or for any liability, damages or expenses caused and/or suffered as a result of any use of and/or posting of and/or appearance of the Comments on this website.\n" +
            "\n" +
            "Platon reserves the right to monitor all Comments and to remove any Comments which can be considered inappropriate, offensive or causes breach of these Terms and Conditions.\n" +
            "\n" +
            "You warrant and represent that:\n" +
            "\n" +
            "You are entitled to post the Comments on our website and have all necessary licenses and consents to do so;\n" +
            "The Comments do not invade any intellectual property right, including without limitation copyright, patent or trademark of any third party;\n" +
            "The Comments do not contain any defamatory, libelous, offensive, indecent or otherwise unlawful material which is an invasion of privacy\n" +
            "The Comments will not be used to solicit or promote business or custom or present commercial activities or unlawful activity.\n" +
            "You hereby grant Platon a non-exclusive license to use, reproduce, edit and authorize others to use, reproduce and edit any of your Comments in any and all forms, formats or media.\n" +
            "\n" +
            "Hyperlinking to our Content\n" +
            "The following organizations may link to our Website without prior written approval:\n" +
            "\n" +
            "Government agencies;\n" +
            "Search engines;\n" +
            "News organizations;\n" +
            "Online directory distributors may link to our Website in the same manner as they hyperlink to the Websites of other listed businesses; and\n" +
            "System wide Accredited Businesses except soliciting non-profit organizations, charity shopping malls, and charity fundraising groups which may not hyperlink to our Web site.\n" +
            "These organizations may link to our home page, to publications or to other Website information so long as the link: (a) is not in any way deceptive; (b) does not falsely imply sponsorship, endorsement or approval of the linking party and its products and/or services; and (c) fits within the context of the linking party’s site.\n" +
            "\n" +
            "We may consider and approve other link requests from the following types of organizations:\n" +
            "\n" +
            "commonly-known consumer and/or business information sources;\n" +
            "dot.com community sites;\n" +
            "associations or other groups representing charities;\n" +
            "online directory distributors;\n" +
            "internet portals;\n" +
            "accounting, law and consulting firms; and\n" +
            "educational institutions and trade associations.\n" +
            "We will approve link requests from these organizations if we decide that: (a) the link would not make us look unfavorably to ourselves or to our accredited businesses; (b) the organization does not have any negative records with us; (c) the benefit to us from the visibility of the hyperlink compensates the absence of Platon; and (d) the link is in the context of general resource information.\n" +
            "\n" +
            "These organizations may link to our home page so long as the link: (a) is not in any way deceptive; (b) does not falsely imply sponsorship, endorsement or approval of the linking party and its products or services; and (c) fits within the context of the linking party’s site.\n" +
            "\n" +
            "If you are one of the organizations listed in paragraph 2 above and are interested in linking to our website, you must inform us by sending an e-mail to Platon. Please include your name, your organization name, contact information as well as the URL of your site, a list of any URLs from which you intend to link to our Website, and a list of the URLs on our site to which you would like to link. Wait 2-3 weeks for a response.\n" +
            "\n" +
            "Approved organizations may hyperlink to our Website as follows:\n" +
            "\n" +
            "By use of our corporate name; or\n" +
            "By use of the uniform resource locator being linked to; or\n" +
            "By use of any other description of our Website being linked to that makes sense within the context and format of content on the linking party’s site.\n" +
            "No use of Platon's logo or other artwork will be allowed for linking absent a trademark license agreement.\n" +
            "\n" +
            "iFrames\n" +
            "Without prior approval and written permission, you may not create frames around our Webpages that alter in any way the visual presentation or appearance of our Website.\n" +
            "\n" +
            "Content Liability\n" +
            "We shall not be hold responsible for any content that appears on your Website. You agree to protect and defend us against all claims that is rising on your Website. No link(s) should appear on any Website that may be interpreted as libelous, obscene or criminal, or which infringes, otherwise violates, or advocates the infringement or other violation of, any third party rights.\n" +
            "\n" +
            "Your Privacy\n" +
            "Please read Privacy Policy\n" +
            "\n" +
            "Reservation of Rights\n" +
            "We reserve the right to request that you remove all links or any particular link to our Website. You approve to immediately remove all links to our Website upon request. We also reserve the right to amen these terms and conditions and it’s linking policy at any time. By continuously linking to our Website, you agree to be bound to and follow these linking terms and conditions.\n" +
            "\n" +
            "Removal of links from our website\n" +
            "If you find any link on our Website that is offensive for any reason, you are free to contact and inform us any moment. We will consider requests to remove links but we are not obligated to or so or to respond to you directly.\n" +
            "\n" +
            "We do not ensure that the information on this website is correct, we do not warrant its completeness or accuracy; nor do we promise to ensure that the website remains available or that the material on the website is kept up to date.\n" +
            "\n" +
            "Disclaimer\n" +
            "To the maximum extent permitted by applicable law, we exclude all representations, warranties and conditions relating to our website and the use of this website. Nothing in this disclaimer will:\n" +
            "\n" +
            "limit or exclude our or your liability for death or personal injury;\n" +
            "limit or exclude our or your liability for fraud or fraudulent misrepresentation;\n" +
            "limit any of our or your liabilities in any way that is not permitted under applicable law; or\n" +
            "exclude any of our or your liabilities that may not be excluded under applicable law.\n" +
            "The limitations and prohibitions of liability set in this Section and elsewhere in this disclaimer: (a) are subject to the preceding paragraph; and (b) govern all liabilities arising under the disclaimer, including liabilities arising in contract, in tort and for breach of statutory duty.\n" +
            "\n" +
            "As long as the website and the information and services on the website are provided free of charge, we will not be liable for any loss or damage of any nature."

}