"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import Link from "next/link";
import { Button, Input } from "@/shared/ui";
import { useRegister } from "@/features/auth/hooks";

const registerSchema = z.object({
  name: z.string().min(2, "이름은 2자 이상이어야 합니다"),
  email: z.string().email("올바른 이메일을 입력해주세요"),
  password: z.string().min(6, "비밀번호는 6자 이상이어야 합니다"),
});

type RegisterForm = z.infer<typeof registerSchema>;

export default function RegisterPage() {
  const registerMutation = useRegister();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterForm>({
    resolver: zodResolver(registerSchema),
  });

  const onSubmit = (data: RegisterForm) => {
    registerMutation.mutate(data);
  };

  return (
    <div className="min-h-screen flex items-center justify-center px-4">
      <div className="w-full max-w-sm">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900">CHome</h1>
          <p className="text-gray-500 mt-2">새 계정을 만드세요</p>
        </div>

        {registerMutation.isError && (
          <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-sm text-red-700">
            회원가입에 실패했습니다. 이미 등록된 이메일일 수 있습니다.
          </div>
        )}

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Input
            label="이름"
            placeholder="홍길동"
            error={errors.name?.message}
            {...register("name")}
          />
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
            placeholder="6자 이상"
            error={errors.password?.message}
            {...register("password")}
          />
          <Button
            type="submit"
            className="w-full"
            size="lg"
            loading={registerMutation.isPending}
          >
            회원가입
          </Button>
        </form>

        <p className="text-center text-sm text-gray-500 mt-6">
          이미 계정이 있으신가요?{" "}
          <Link href="/login" className="text-blue-600 hover:underline">
            로그인
          </Link>
        </p>
      </div>
    </div>
  );
}
