package com.example.medora

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AboutUsActivity : AppCompatActivity() {

    private lateinit var ivBack: ImageView
    private lateinit var tvContent: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        ivBack = findViewById(R.id.ivBack)
        tvContent = findViewById(R.id.tvContent)

        ivBack.setOnClickListener { finish() }

        loadAboutContent()
    }

    private fun loadAboutContent() {
        val aboutText = """
            About Medora
            
            Welcome to Medora - Your Complete Healthcare Companion
            
            Our Mission
            
            At Medora, we believe healthcare should be accessible, affordable, and patient-centric. Our mission is to bridge the gap between patients and quality healthcare services through innovative technology solutions.
            
            What We Offer
            
            🏥 Doctor Consultations
            Connect with verified doctors across various specializations. Book in-person visits, video consultations, or phone consultations at your convenience.
            
            💊 Medicine Delivery
            Order prescription and over-the-counter medicines from verified pharmacies. Get doorstep delivery with quality assurance.
            
            🧪 Lab Tests
            Book diagnostic tests from certified labs. Get home sample collection and digital reports delivered quickly.
            
            📊 Health Tracking
            Monitor your health metrics including steps, heart rate, sleep patterns, weight, and more. Get personalized insights and recommendations.
            
            🤖 AI Health Assistant
            Our AI-powered assistant provides instant health information, medication reminders, and preliminary assessments available 24/7.
            
            📱 Emergency Services
            Quick access to emergency contacts, ambulance services, and nearby hospitals during critical situations.
            
            Our Story
            
            Founded in 2025, Medora was born from a simple idea: healthcare should be easy and accessible to everyone. Our team of healthcare professionals, technologists, and designers came together to create a platform that puts patients first.
            
            Why Choose Medora?
            
            ✓ Verified Professionals
            All doctors, pharmacies, and labs are thoroughly verified and registered with appropriate regulatory bodies.
            
            ✓ Secure & Private
            Your health data is encrypted and protected with industry-leading security measures. We never share your information without consent.
            
            ✓ Transparent Pricing
            No hidden charges. Clear pricing for all services with multiple payment options.
            
            ✓ 24/7 Support
            Our customer support team is available round the clock to assist you.
            
            ✓ Quality Assurance
            We partner only with certified healthcare providers who meet our strict quality standards.
            
            Our Values
            
            Patient First
            Every decision we make prioritizes patient wellbeing and satisfaction.
            
            Trust & Transparency
            We believe in open communication and honest practices in all our dealings.
            
            Innovation
            We continuously improve and adopt new technologies to enhance healthcare delivery.
            
            Accessibility
            Healthcare should reach everyone, regardless of location or economic status.
            
            Quality
            We never compromise on the quality of services and care provided.
            
            Our Team
            
            Medora is powered by a diverse team of:
            - Experienced healthcare professionals
            - Skilled software engineers
            - Creative designers
            - Dedicated customer support specialists
            - Medical advisors and consultants
            
            Recognition
            
            - Featured in Top Healthcare Apps 2025
            - Trusted by 100,000+ users
            - 4.8★ average rating on Play Store
            - Winner of Healthcare Innovation Award 2025
            
            Our Partners
            
            We collaborate with leading:
            - Hospitals and clinics
            - Diagnostic laboratories
            - Pharmaceutical companies
            - Insurance providers
            - Healthcare technology providers
            
            Social Responsibility
            
            We believe in giving back to society through:
            - Free health checkup camps
            - Healthcare awareness programs
            - Subsidized services for economically weaker sections
            - Partnership with NGOs for healthcare access
            
            Future Vision
            
            We aim to:
            - Expand services to tier-2 and tier-3 cities
            - Integrate with more healthcare providers
            - Launch wellness programs and health insurance
            - Introduce telemedicine in rural areas
            - Develop preventive care solutions
            
            Contact Us
            
            We'd love to hear from you!
            
            📧 Email: info@medora.com
            📞 Phone: +91 1800-XXX-XXXX
            🌐 Website: www.medora.com
            
            Office Address:
            Medora Healthcare Pvt. Ltd.
            [Your Office Address]
            [City, State - PIN Code]
            
            Follow Us:
            Facebook: /medorahealth
            Twitter: @medorahealth
            Instagram: @medora.health
            LinkedIn: /company/medora
            
            For Business Inquiries:
            📧 business@medora.com
            
            For Media Inquiries:
            📧 media@medora.com
            
            Join Our Journey
            
            We're always looking for talented individuals who share our passion for healthcare innovation. Check out career opportunities at careers@medora.com
            
            Thank you for choosing Medora. Together, let's build a healthier tomorrow!
            
            Version 1.0.0
            © 2025 Medora Healthcare Pvt. Ltd. All rights reserved.
        """.trimIndent()

        tvContent.text = aboutText
    }
}
