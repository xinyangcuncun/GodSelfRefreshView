# GodSelfRefreshView
本项目是个自定义的下拉刷新、上拉加载框架，支持原生webview、RecyclerView、ListView、ScrollView

##V1.0

WebView仅支持原生

##V2.0

WebView支持腾讯SDK

##依赖使用

>第一步

    allprojects {
    		repositories {
    			...
    			maven { url 'https://jitpack.io' }
    		}
    	}
    
>第二步
 
     dependencies {
            compile 'com.github.xinyangcuncun:GodSelfRefreshView:V1.0'
          }

##API介绍

>使用下拉刷新（非自定义）

    //开启下拉刷新
    godSeflRefreshView.setBaseHeaderManager();
    //下拉刷新监听
    godSeflRefreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {
                @Override
                public void onHeaderRefresh(GodSeflRefreshView view) {
                   
                }
            });
    //刷新完成
    godSeflRefreshView.onHeaderRefreshComplete();
    
>使用上拉加载（非自定义）

      //开启上拉加载
       godSeflRefreshView.setBaseFooterManager();
      //上拉加载监听
      godSeflRefreshView.setOnFooterRefreshListener(new OnFooterRefreshListener() {
                  @Override
                  public void onFooterRefresh(GodSeflRefreshView view) {
                      
                  }
              });
       //加载完成
       godSeflRefreshView.onFooterRefreshComplete();
       //加载到底部
       godSeflRefreshView.onFooterRefreshOver();
       
    
