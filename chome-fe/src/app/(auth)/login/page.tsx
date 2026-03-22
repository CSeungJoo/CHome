"use client";

import { Suspense } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import Link from "next/link";
import { useSearchParams } from "next/navigation";
import { Button, Input } from "@/shared/ui";
import { useLogin } from "@/features/auth/hooks";

const loginSchema = z.object({
  email: z.string().email("올바른 이메일을 입력해주세요"),
  password: z.string().min(1, "비밀번호를 입력해주세요"),
});

type LoginForm = z.infer<typeof loginSchema>;

export default function LoginPage() {
  return (
    <Suspense>
      <LoginContent />
    </Suspense>
  );
}

function LoginContent() {
  const searchParams = useSearchParams();
  const registered = searchParams.get("registered");
  const loginMutation = useLogin();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginForm>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = (data: LoginForm) => {
    loginMutation.mutate(data);
  };

  return (
    <div className="min-h-screen flex items-center justify-center px-4">
      <div className="w-full max-w-sm">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900">CHome</h1>
          <p className="text-gray-500 mt-2">스마트 홈에 로그인하세요</p>
        </div>

        {registered && (
          <div className="mb-4 p-3 bg-green-50 border border-green-200 rounded-lg text-sm text-green-700">
            회원가입이 완료되었습니다. 이메일 인증 후 로그인해주세요.
          </div>
        )}

        {loginMutation.isError && (
          <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-sm text-red-700">
            로그인에 실패했습니다. 이메일과 비밀번호를 확인해주세요.
          </div>
        )}

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Input
            label="이메일"
            type="email"
            placeholder="email@example.com"
            error={errors.email?.message}
            {...register("email")}
          />
          <Input
            label="비밀번호"
            type="password"
            placeholder="비밀번호 입력"
            error={errors.password?.message}
            {...register("password")}
          />
          <Button
            type="submit"
            className="w-full"
            size="lg"
            loading={loginMutation.isPending}
          >
            로그인
          </Button>
        </form>

        <p className="text-center text-sm text-gray-500 mt-6">
          계정이 없으신가요?{" "}
          <Link href="/register" className="text-blue-600 hover:underline">
            회원가입
          </Link>
        </p>
      </div>
    </div>
  );
}
