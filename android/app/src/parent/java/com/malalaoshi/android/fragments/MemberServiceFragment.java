package com.malalaoshi.android.fragments;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.activitys.MemberActivity;
import com.malalaoshi.android.api.LearningReportApi;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.event.BusEvent;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.entity.Report;
import com.malalaoshi.android.entity.Subject;
import com.malalaoshi.android.report.ReportActivity;
import com.malalaoshi.android.result.ReportListResult;
import com.malalaoshi.android.util.AuthUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by kang on 16/5/16.
 * 会员专享
 */

public class MemberServiceFragment extends BaseFragment {

    AnimationDrawable refreshAnimation = null;

    @Bind(R.id.ll_refresh_refreshing)
    protected LinearLayout llRefreshRefreshing;

    @Bind(R.id.iv_refresh_refreshing)
    protected ImageView ivRefreshRefreshing;

    @Bind(R.id.tv_refresh_refreshing)
    protected TextView tvRefreshRefreshing;

    @Bind(R.id.sub_non_learning_report)
    protected RelativeLayout rlNonLearningReport;

    @Bind(R.id.sub_learning_report)
    protected RelativeLayout rlLearningReport;

    @Bind(R.id.tv_subject)
    protected TextView tvSubject;

    @Bind(R.id.tv_answer_number)
    protected TextView tvAnswerNumber;

    @Bind(R.id.tv_correct_rate)
    protected TextView tvCorrectRate;

    @Bind(R.id.tv_open_learning_report)
    protected TextView tvOpenLearReport;

    @Bind(R.id.tv_report_prompt)
    protected TextView tvReportPrompt;

    @Bind(R.id.tv_report_submit)
    protected TextView tvReportSubmit;

    enum EnumReportStatus {
        LONGING, LONGFAILED, NOTSIGNIN, NOTSIGNUP, EMPTYREPORT, REPORT;
    }

    private EnumReportStatus reportStatus = EnumReportStatus.LONGING;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_service,container, false);
        ButterKnife.bind(this,view);
        EventBus.getDefault().register(this);
        initView();
        initData();
        setEvent();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    private void setEvent() {

    }

    private void initData() {
        loadData();
    }

    private void initView() {
        refreshAnimation = (AnimationDrawable)ivRefreshRefreshing.getDrawable();
    }

    private void loadData() {
        if (!UserManager.getInstance().isLogin()){
            showNotSignInView();
        }else{
            showLoadingView();
            ApiExecutor.exec(new FetchReportRequest(this));
        }
    }

    public void onEventMainThread(BusEvent event) {
        Log.e("BUS_EVENT_LOGIN_SUCCESS","MemberSerVices"+event.getEventType());
        switch (event.getEventType()) {
            case BusEvent.BUS_EVENT_LOGOUT_SUCCESS:
            case BusEvent.BUS_EVENT_LOGIN_SUCCESS:
                reloadData();
                break;

        }
    }

    @OnClick(R.id.ll_refresh_refreshing)//刷新
    public void onClickRefresh(View view){
        if (reportStatus==EnumReportStatus.LONGFAILED){
            reloadData();
        }
    }

    @OnClick(R.id.tv_report_submit)//登录或查看样本
    public void onClickNoReport(View view){
        if (reportStatus==EnumReportStatus.NOTSIGNIN){
            openLoginActivity();
        }else if (reportStatus==EnumReportStatus.NOTSIGNUP){
            openSampleReport();
        }else if (reportStatus==EnumReportStatus.EMPTYREPORT){
            openSampleReport();
        }
    }

    @OnClick(R.id.tv_open_learning_report)//查看样本
    public void onClickOpenReport(View view){
        if (reportStatus==EnumReportStatus.REPORT){
            openLearningReport();
        }
    }

    //查看学习报告
    private void openLearningReport() {
    }

    //查看学习报告样本
    private void openSampleReport() {
        getActivity().startActivity(new Intent(getActivity(), ReportActivity.class));
    }

    //登录
    private void openLoginActivity() {
        AuthUtils.redirectLoginActivity(getContext());
    }

    //重新加载
    private void reloadData() {
        loadData();
    }

    private void showLoadingView(){
        rlNonLearningReport.setVisibility(View.GONE);
        rlLearningReport.setVisibility(View.GONE);
        llRefreshRefreshing.setVisibility(View.VISIBLE);
        llRefreshRefreshing.setOnClickListener(null);
        ivRefreshRefreshing.setImageDrawable(refreshAnimation);
        refreshAnimation.start();
        tvRefreshRefreshing.setText("正在加载数据···");
        reportStatus = EnumReportStatus.LONGING;
    }

    private void showLoadFailedView(){
        refreshAnimation.stop();
        rlNonLearningReport.setVisibility(View.GONE);
        rlLearningReport.setVisibility(View.GONE);
        llRefreshRefreshing.setVisibility(View.VISIBLE);
        ivRefreshRefreshing.setImageDrawable(getResources().getDrawable(R.drawable.ic_course));
        tvRefreshRefreshing.setText("加载失败,点击重试!");
        reportStatus = EnumReportStatus.LONGFAILED;
    }

    private void showNotSignInView(){
        refreshAnimation.stop();
        rlNonLearningReport.setVisibility(View.VISIBLE);
        rlLearningReport.setVisibility(View.GONE);
        llRefreshRefreshing.setVisibility(View.GONE);
        tvReportPrompt.setText("登录可查看专属学习报告哦···");
        tvReportSubmit.setText("登录");
        reportStatus = EnumReportStatus.NOTSIGNIN;
    }

    private void showNotSignUpView(){
        refreshAnimation.stop();
        rlNonLearningReport.setVisibility(View.VISIBLE);
        rlLearningReport.setVisibility(View.GONE);
        llRefreshRefreshing.setVisibility(View.GONE);
        tvReportPrompt.setText("您还未报名,先看看其他样本报告吧···");
        tvReportSubmit.setText("查看学习报告样本");
        reportStatus = EnumReportStatus.NOTSIGNUP;
    }

    private void showEmptyReportView(){
        refreshAnimation.stop();
        rlNonLearningReport.setVisibility(View.VISIBLE);
        rlLearningReport.setVisibility(View.GONE);
        llRefreshRefreshing.setVisibility(View.GONE);
        tvReportPrompt.setText("当前科目暂未开通学习报告,敬请期待···");
        tvReportSubmit.setText("查看数学学习报告样本");
        reportStatus = EnumReportStatus.EMPTYREPORT;
    }

    private void showReportView(Report report){
        refreshAnimation.stop();
        rlNonLearningReport.setVisibility(View.GONE);
        rlLearningReport.setVisibility(View.VISIBLE);
        llRefreshRefreshing.setVisibility(View.GONE);
        Subject subject = Subject.getSubjectById(report.getSubject_id());
        if (subject!=null){
            tvSubject.setText(subject.getName());
        }else{
            tvSubject.setText("");
        }
        tvAnswerNumber.setText(report.getTotal_nums()+"");
        int rate = 0;
        if (report.getTotal_nums()>0){
            rate = report.getRight_nums()*100/report.getTotal_nums();
        }
        tvCorrectRate.setText(rate+"%");
        reportStatus = EnumReportStatus.REPORT;
    }

    private void dealResponse(ReportListResult response) {
        if (response.getCode()==0){
            List<Report> reports = response.getResults();
            if (reports.size()>0){
                Report report = null;
                for (int i=0;i<reports.size();i++){
                    if (reports.get(i).isSupported()){
                        report = reports.get(i);
                        break;
                    }
                }
                if (report!=null&&report.getTotal_nums()>0){
                    //update ui
                    showReportView(report);
                }else{
                    showEmptyReportView();
                }
            }else{
                showNotSignUpView();
            }
        }else{
            showLoadFailedView();
        }
    }


    private static final class FetchReportRequest extends BaseApiContext<MemberServiceFragment, ReportListResult> {

        public FetchReportRequest(MemberServiceFragment memberServiceFragment) {
            super(memberServiceFragment);
        }

        @Override
        public ReportListResult request() throws Exception {
            return new LearningReportApi().get();
        }

        @Override
        public void onApiSuccess(@NonNull ReportListResult response) {
            if (response!=null){
                get().dealResponse(response);
            }else{
                get().showLoadFailedView();
            }
        }

        @Override
        public void onApiFailure(Exception exception) {
            get().showLoadFailedView();
        }

    }

    /*************************会员专享*************************/
    @OnClick(R.id.tv_with_read)//自习陪读
    public void onClickWithRead(View view){
        openMemberServiceAvtivity(0);
    }

    @OnClick(R.id.tv_learning_report)//学习报告
    public void onClickLearningReport(View view){
        openMemberServiceAvtivity(1);
    }

    @OnClick(R.id.tv_counseling)//心理辅导
    public void onClickCounseling(View view){
        openMemberServiceAvtivity(2);
    }

    @OnClick(R.id.tv_lectures)//特色讲座
    public void onClickLectures(View view){
        openMemberServiceAvtivity(3);
    }

    @OnClick(R.id.tv_exam_explain)//考前串讲
    public void onClickExamExplain(View view){
        openMemberServiceAvtivity(4);
    }

    @OnClick(R.id.tv_mistake)//错题本
    public void onClickMistake(View view){
        openMemberServiceAvtivity(5);
    }

    @OnClick(R.id.tv_spps_evaluation)//SPPS测评
    public void onClickSppsEvaluation(View view){
        openMemberServiceAvtivity(6);
    }

    @OnClick(R.id.tv_expect_more)//敬请期待
    public void onClickExpectMore(View view){
        openMemberServiceAvtivity(7);
    }

    private void openMemberServiceAvtivity(int position){
        Intent intent = new Intent(getContext(), MemberActivity.class);
        intent.putExtra(MemberActivity.EXTRA_CURRETN_POSITION,position);
        startActivity(intent);
    }

    @Override
    public String getStatName() {
        return "会员专享";
    }

}