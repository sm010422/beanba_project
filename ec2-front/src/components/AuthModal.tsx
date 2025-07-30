
import React, { useState } from 'react';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { SocialLoginButtons } from './auth/SocialLoginButtons';
import LocationDisplay from './auth/LocationDisplay';
import AuthForm from './auth/AuthForm';
import EmailVerification from './auth/EmailVerification';
import EmailCodeVerification from './auth/EmailCodeVerification';
import SignupForm from './auth/SignupForm';
import FindIdForm from './auth/FindIdForm';
import FindPasswordForm from './auth/FindPasswordForm';
import PasswordCodeVerification from './auth/PasswordCodeVerification';
import PasswordChangeForm from './auth/PasswordChangeForm';
import { useNavigate } from 'react-router-dom';

interface AuthModalProps {
  isOpen: boolean;
  onClose: () => void;
}

type AuthStep = 'login' | 'email-verification' | 'code-verification' | 'signup' | 'find-id' | 'find-password' | 'password-code-verification' | 'password-change';

const AuthModal = ({ isOpen, onClose }: AuthModalProps) => {
  const [currentStep, setCurrentStep] = useState<AuthStep>('login');
  const [verifiedEmail, setVerifiedEmail] = useState('');
  const [passwordResetData, setPasswordResetData] = useState({
    memberId: '',
    email: ''
  });
  const navigate = useNavigate();

  const handleSubmit = (formData: any) => {
    console.log('Form submitted:', formData);
    onClose();
  };

  const handleSocialLogin = (provider: string) => {
    console.log(`${provider} login clicked`);
    onClose();
  };

  const handleEmailSent = (email: string) => {
    setVerifiedEmail(email);
    setCurrentStep('code-verification');
  };

  const handleEmailVerified = () => {
    setCurrentStep('signup');
  };

  const handleSignupSuccess = () => {
    setCurrentStep('login');
    setVerifiedEmail('');
    onClose();
    navigate('/');
  };

  const handlePasswordCodeSent = (memberId: string, email: string) => {
    setPasswordResetData({ memberId, email });
    setCurrentStep('password-code-verification');
  };

  const handlePasswordCodeVerified = () => {
    setCurrentStep('password-change');
  };

  const handlePasswordChanged = () => {
    setCurrentStep('login');
    setPasswordResetData({ memberId: '', email: '' });
    onClose();
  };

  const handleToggleMode = () => {
    if (currentStep === 'login') {
      setCurrentStep('email-verification');
    } else {
      setCurrentStep('login');
      setVerifiedEmail('');
    }
  };

  const handleBackToEmailInput = () => {
    setCurrentStep('email-verification');
    setVerifiedEmail('');
  };

  const handleBackToPasswordInput = () => {
    setCurrentStep('find-password');
  };

  // 모달이 닫힐 때 상태 초기화
  const handleClose = () => {
    if (currentStep !== 'email-verification' && currentStep !== 'code-verification' && currentStep !== 'password-code-verification') {
      setCurrentStep('login');
      setVerifiedEmail('');
      setPasswordResetData({ memberId: '', email: '' });
    }
    onClose();
    
    setTimeout(() => {
      setCurrentStep('login');
      setVerifiedEmail('');
      setPasswordResetData({ memberId: '', email: '' });
    }, 200);
  };

  const getModalTitle = () => {
    switch (currentStep) {
      case 'login':
        return '로그인';
      case 'email-verification':
        return '이메일 인증';
      case 'code-verification':
        return '계정 인증';
      case 'signup':
        return '회원가입';
      case 'find-id':
        return '아이디 찾기';
      case 'find-password':
        return '비밀번호 찾기';
      case 'password-code-verification':
        return '비밀번호 찾기 인증';
      case 'password-change':
        return '비밀번호 변경';
      default:
        return '로그인';
    }
  };

  const getMaxWidth = () => {
    return currentStep === 'signup' ? 'sm:max-w-6xl' : 'sm:max-w-md';
  };

  return (
    <Dialog open={isOpen} onOpenChange={handleClose}>
      <DialogContent className={getMaxWidth()}>
        <DialogHeader>
          <DialogTitle className="text-center text-xl font-bold text-green-700">
            {getModalTitle()}
          </DialogTitle>
        </DialogHeader>

        <div className={`flex gap-8 p-6 ${currentStep !== 'signup' ? 'justify-center' : ''}`}>
          {/* Left Side - Form */}
          <div className={`space-y-6 ${currentStep !== 'signup' ? 'w-full max-w-sm' : 'flex-1'}`}>
            {currentStep === 'email-verification' ? (
              <EmailVerification onEmailSent={handleEmailSent} />
            ) : currentStep === 'code-verification' ? (
              <EmailCodeVerification 
                email={verifiedEmail}
                onVerified={handleEmailVerified}
                onBack={handleBackToEmailInput}
              />
            ) : currentStep === 'signup' ? (
              <SignupForm
                email={verifiedEmail}
                onSuccess={handleSignupSuccess}
              />
            ) : currentStep === 'find-id' ? (
              <FindIdForm onBack={() => setCurrentStep('login')} />
            ) : currentStep === 'find-password' ? (
              <FindPasswordForm 
                onBack={() => setCurrentStep('login')}
                onCodeSent={handlePasswordCodeSent}
              />
            ) : currentStep === 'password-code-verification' ? (
              <PasswordCodeVerification
                memberId={passwordResetData.memberId}
                email={passwordResetData.email}
                onVerified={handlePasswordCodeVerified}
                onBack={handleBackToPasswordInput}
              />
            ) : currentStep === 'password-change' ? (
              <PasswordChangeForm onSuccess={handlePasswordChanged} />
            ) : (
              <>
                <SocialLoginButtons />

                <div className="relative">
                  <div className="absolute inset-0 flex items-center">
                    <span className="w-full border-t border-gray-300" />
                  </div>
                  <div className="relative flex justify-center text-xs uppercase">
                    <span className="bg-white px-2 text-gray-500">또는</span>
                  </div>
                </div>

                <AuthForm 
                  isLogin={currentStep === 'login'} 
                  onSubmit={handleSubmit}
                  prefilledEmail={verifiedEmail}
                  onClose={handleClose}
                  onFindId={() => setCurrentStep('find-id')}
                  onFindPassword={() => setCurrentStep('find-password')}
                />
              </>
            )}

            {(currentStep === 'login' || currentStep === 'signup') && (
              <div className="text-center">
                <button
                  type="button"
                  onClick={handleToggleMode}
                  className="text-sm text-green-600 hover:text-green-700 underline"
                >
                  {currentStep === 'login' ? '계정이 없으신가요? 회원가입' : '이미 계정이 있으신가요? 로그인'}
                </button>
              </div>
            )}
          </div>

          {/* Right Side - Location Map for Signup */}
          {currentStep === 'signup' && (
            <LocationDisplay isOpen={isOpen && currentStep === 'signup'} />
          )}
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default AuthModal;
