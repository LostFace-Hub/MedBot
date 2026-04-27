package com.example.medora

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TermsConditionsActivity : AppCompatActivity() {

    private lateinit var ivBack: ImageView
    private lateinit var tvContent: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_conditions)

        ivBack = findViewById(R.id.ivBack)
        tvContent = findViewById(R.id.tvContent)

        ivBack.setOnClickListener { finish() }

        loadTermsContent()
    }

    private fun loadTermsContent() {
        val termsText = """
            Terms and Conditions
            
            Last Updated: November 19, 2025
            
            1. Acceptance of Terms
            By accessing and using the Medora application, you accept and agree to be bound by the terms and provision of this agreement.
            
            2. Use License
            Permission is granted to temporarily download one copy of Medora app for personal, non-commercial transitory viewing only.
            
            3. Medical Disclaimer
            - Medora is a healthcare assistance platform and not a replacement for professional medical advice
            - Always consult with qualified healthcare professionals for medical decisions
            - Emergency situations require immediate medical attention - call emergency services
            
            4. User Responsibilities
            - Provide accurate and complete information
            - Maintain confidentiality of your account credentials
            - Use the platform responsibly and lawfully
            - Report any suspicious activity or security breaches
            
            5. Privacy and Data Protection
            - We collect and process personal health information as described in our Privacy Policy
            - Your data is encrypted and stored securely
            - We comply with applicable healthcare data protection regulations
            
            6. Appointment and Consultation Terms
            - Appointments are subject to doctor availability
            - Cancellations must be made at least 2 hours in advance
            - Video consultations require stable internet connection
            - Recording of consultations is prohibited without consent
            
            7. Medicine Orders
            - Prescription medicines require valid prescription upload
            - Orders are subject to availability and verification
            - Delivery times are estimates and may vary
            - Returns and refunds follow our return policy
            
            8. Payment Terms
            - All prices are in Indian Rupees (INR)
            - Payment must be made at the time of service booking
            - Refunds will be processed within 7-10 business days
            - We use secure payment gateways for transactions
            
            9. Intellectual Property
            - All content, features, and functionality are owned by Medora
            - You may not reproduce, distribute, or create derivative works
            - Trademarks and logos are property of their respective owners
            
            10. Limitation of Liability
            - Medora is not liable for any indirect, incidental, or consequential damages
            - We do not guarantee uninterrupted or error-free service
            - Maximum liability is limited to the amount paid for services
            
            11. Termination
            We reserve the right to:
            - Suspend or terminate accounts for violations
            - Modify or discontinue services with notice
            - Refuse service to anyone for any reason
            
            12. Changes to Terms
            - We reserve the right to modify these terms at any time
            - Continued use after changes constitutes acceptance
            - We will notify users of significant changes
            
            13. Governing Law
            These terms are governed by the laws of India. Any disputes will be resolved in courts of [Your City].
            
            14. Contact Information
            For questions about these terms:
            - Email: support@medora.com
            - Phone: +91 1800-XXX-XXXX
            - Address: [Your Business Address]
            
            15. Severability
            If any provision of these terms is found to be unenforceable, the remaining provisions will continue in full force and effect.
            
            By using Medora, you acknowledge that you have read, understood, and agree to be bound by these Terms and Conditions.
        """.trimIndent()

        tvContent.text = termsText
    }
}
