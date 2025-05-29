<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ include file="WEB-INF/header.jsp"%>

<!-- Contact Start -->
<div class="container-fluid">
    <h2 class="section-title position-relative text-uppercase mx-xl-5 mb-4">
        <span class="bg-secondary pr-3"><fmt:message key="title.contactUs" /></span>
    </h2>
    <div class="row px-xl-5">
        <div class="col-lg-7 mb-5">
            <div class="contact-form bg-light p-30">
                <div id="success"></div>
                <form name="sentMessage" id="contactForm" novalidate="novalidate">
                    <div class="control-group">
                        <input type="text" class="form-control" id="name" 
                            placeholder="<fmt:message key='placeholder.name' />"
                            required="required" 
                            data-validation-required-message="<fmt:message key='validation.name' />" />
                        <p class="help-block text-danger"></p>
                    </div>
                    <div class="control-group">
                        <input type="email" class="form-control" id="email" 
                            placeholder="<fmt:message key='placeholder.email' />"
                            required="required" 
                            data-validation-required-message="<fmt:message key='validation.email' />" />
                        <p class="help-block text-danger"></p>
                    </div>
                    <div class="control-group">
                        <input type="text" class="form-control" id="subject" 
                            placeholder="<fmt:message key='placeholder.subject' />"
                            required="required" 
                            data-validation-required-message="<fmt:message key='validation.subject' />" />
                        <p class="help-block text-danger"></p>
                    </div>
                    <div class="control-group">
                        <textarea class="form-control" rows="8" id="message" 
                            placeholder="<fmt:message key='placeholder.message' />"
                            required="required" 
                            data-validation-required-message="<fmt:message key='validation.message' />"></textarea>
                        <p class="help-block text-danger"></p>
                    </div>
                    <div>
                        <button class="btn btn-primary py-2 px-4" type="submit" id="sendMessageButton">
                            <fmt:message key="button.sendMessage" />
                        </button>
                    </div>
                </form>
            </div>
        </div>
        <div class="col-lg-5 mb-5">
            <div class="bg-light p-30 mb-30">
                <iframe style="width: 100%; height: 250px;"
                    src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3001156.4288297426!2d-111.01371936852176!3d70.72876761954724!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x4ccc4bf0f123a5a9%3A0xddcfc6c1de189567!2sNew%20York%2C%20USA!5e0!3m2!1sen!2sbd!4v1603794290143!5m2!1sen!2sbd"
                    frameborder="0" style="border:0;" allowfullscreen="" aria-hidden="false" tabindex="0"></iframe>
            </div>
            <div class="bg-light p-30 mb-3">
                <p class="mb-2"><i class="fa fa-map-marker-alt text-primary mr-3"></i>
                    123 Street, New York, USA
                </p>
                <p class="mb-2"><i class="fa fa-envelope text-primary mr-3"></i>
                    info@example.com
                </p>
                <p class="mb-2"><i class="fa fa-phone-alt text-primary mr-3"></i>
                    +012 345 67890
                </p>
            </div>
        </div>
    </div>
</div>
<!-- Contact End -->

<%@ include file="WEB-INF/footer.jsp" %>
