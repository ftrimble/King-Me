from django.conf.urls import patterns, url

from rideupload import views

urlpatterns = patterns('', url(r'^$', views.feed, name='feed'))
