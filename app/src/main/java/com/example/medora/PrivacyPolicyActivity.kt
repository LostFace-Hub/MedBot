package com.example.medora

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PrivacyPolicyActivity : AppCompatActivity() {

    private lateinit var ivBack: ImageView
    private lateinit var tvContent: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        ivBack = findViewById(R.id.ivBack)
        tvContent = findViewById(R.id.tvContent)

        ivBack.setOnClickListener { finish() }

        loadPrivacyContent()
    }

    private fun loadPrivacyContent() {
        val privacyText = """
            Privacy Policy
            
            Last Updated: November 19, 2025
            
            1. Introduction
            Medora ("we," "our," or "us") is committed to protecting your privacy. This Privacy Policy explains how we collect, use, disclose, and safeguard your information when you use our mobile application.
            
            2. Information We Collect
            
            2.1 Personal Information
            - Full name, email address, phone number
            - Date of birth, gender, blood group
            - Height, weight, and other health metrics
            - Profile picture and identification documents
            
            2.2 Health Information
            - Medical history and conditions
            - Prescriptions and medications
            - Lab test results and reports
            - Consultation notes and diagnoses
            - Health tracking data (steps, heart rate, sleep)
            
            2.3 Usage Information
            - Device information (model, OS version)
            - IP address and location data
            - App usage patterns and preferences
            - Search queries and interactions
            
            2.4 Payment Information
            - Payment method details (encrypted)
            - Transaction history
            - Billing addresses
            
            3. How We Use Your Information
            
            3.1 To Provide Services
            - Process appointments and consultations
            - Deliver medicines and healthcare products
            - Generate health reports and analytics
            - Provide AI-powered health assistance
            
            3.2 To Improve Services
            - Analyze usage patterns
            - Develop new features
            - Enhance user experience
            - Troubleshoot issues
            
            3.3 To Communicate
            - Send appointment reminders
            - Provide order updates
            - Share health tips and notifications
            - Respond to inquiries and support requests
            
            3.4 For Security
            - Verify identity and prevent fraud
            - Protect against unauthorized access
            - Comply with legal obligations
            - Enforce our terms and policies
            
            4. Information Sharing
            
            We may share your information with:
            
            4.1 Healthcare Providers
            - Doctors for consultations
            - Pharmacies for medicine orders
            - Labs for test processing
            
            4.2 Service Providers
            - Payment processors
            - Cloud storage providers
            - Analytics services
            - Delivery partners
            
            4.3 Legal Requirements
            - When required by law
            - To protect rights and safety
            - In case of legal proceedings
            
            We DO NOT:
            - Sell your personal information
            - Share data with advertisers
            - Use health data for marketing
            
            5. Data Security
            
            We implement industry-standard security measures:
            - End-to-end encryption for sensitive data
            - Secure HTTPS connections
            - Regular security audits
            - Access controls and authentication
            - Encrypted data storage
            
            6. Your Rights
            
            You have the right to:
            - Access your personal information
            - Correct inaccurate data
            - Delete your account and data
            - Export your data
            - Opt-out of marketing communications
            - Withdraw consent at any time
            
            To exercise these rights, contact us at privacy@medora.com
            
            7. Data Retention
            
            We retain your information:
            - Active account: For as long as you use our services
            - Health records: As required by medical regulations (minimum 7 years)
            - Payment data: As required by financial regulations
            - After deletion: Some data may be retained for legal compliance
            
            8. Children's Privacy
            
            Medora is not intended for children under 13. We do not knowingly collect information from children. If you believe a child has provided us information, contact us immediately.
            
            9. Third-Party Services
            
            Our app may contain links to third-party services. We are not responsible for their privacy practices. Please review their policies before sharing information.
            
            10. International Data Transfers
            
            Your information may be transferred to and processed in countries outside your residence. We ensure adequate safeguards are in place for such transfers.
            
            11. Cookies and Tracking
            
            We use cookies and similar technologies to:
            - Maintain login sessions
            - Remember preferences
            - Analyze app performance
            - Improve user experience
            
            You can manage cookie preferences in your device settings.
            
            12. Changes to Privacy Policy
            
            We may update this policy periodically. We will notify you of significant changes via:
            - In-app notifications
            - Email notifications
            - App update notes
            
            Continued use after changes constitutes acceptance of the updated policy.
            
            13. Contact Us
            
            For privacy-related questions or concerns:
            
            Email: privacy@medora.com
            Phone: +91 1800-XXX-XXXX
            Address: [Your Business Address]
            
            Data Protection Officer: [Name]
            Email: dpo@medora.com
            
            14. Complaints
            
            If you believe your privacy rights have been violated, you may file a complaint with:
            - Our Data Protection Officer
            - The appropriate data protection authority in your jurisdiction
            
            15. Consent
            
            By using Medora, you consent to the collection, use, and sharing of your information as described in this Privacy Policy.
            
            Last Reviewed: November 19, 2025
        """.trimIndent()

        tvContent.text = privacyText
    }
}
