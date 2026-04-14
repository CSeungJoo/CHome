"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useAuthStore } from "@/stores/auth-store";
import {
  HiOutlineHome,
  HiOutlineCube,
  HiOutlineCpuChip,
  HiOutlineArrowRightOnRectangle,
  HiOutlineBars3,
  HiOutlineXMark,
} from "react-icons/hi2";

const navItems = [
  { href: "/dashboard", label: "대시보드", icon: HiOutlineHome },
  { href: "/hubs", label: "허브 관리", icon: HiOutlineCube },
  { href: "/devices", label: "디바이스", icon: HiOutlineCpuChip },
];

export function Sidebar() {
  const pathname = usePathname();
  const router = useRouter();
  const clearAuth = useAuthStore((s) => s.clearAuth);
  const [mobileOpen, setMobileOpen] = useState(false);

  // 경로 변경 시 모바일 메뉴 닫기
  useEffect(() => {
    setMobileOpen(false);
  }, [pathname]);

  const handleLogout = () => {
    clearAuth();
    router.push("/login");
  };

  const sidebarContent = (
    <>
      <div className="p-6 flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-gray-100">CHome</h1>
          <p className="text-xs text-gray-500 mt-1">Smart Home Dashboard</p>
        </div>
        <button
          onClick={() => setMobileOpen(false)}
          className="md:hidden text-gray-400 hover:text-gray-200"
        >
          <HiOutlineXMark className="w-6 h-6" />
        </button>
      </div>

      <nav className="flex-1 px-3">
        {navItems.map((item) => {
          const isActive = pathname.startsWith(item.href);
          return (
            <Link
              key={item.href}
              href={item.href}
              className={`flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium mb-1 transition-colors ${
                isActive
                  ? "bg-blue-600/20 text-blue-400"
                  : "text-gray-400 hover:bg-gray-800 hover:text-gray-200"
              }`}
            >
              <item.icon className="w-5 h-5" />
              {item.label}
            </Link>
          );
        })}
      </nav>

      <div className="p-3 border-t border-gray-800">
        <button
          onClick={handleLogout}
          className="flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium text-gray-400 hover:bg-gray-800 hover:text-gray-200 w-full transition-colors"
        >
          <HiOutlineArrowRightOnRectangle className="w-5 h-5" />
          로그아웃
        </button>
      </div>
    </>
  );

  return (
    <>
      {/* 모바일 햄버거 버튼 */}
      <button
        onClick={() => setMobileOpen(true)}
        className="md:hidden fixed top-4 left-4 z-40 bg-gray-900 border border-gray-800 rounded-lg p-2 text-gray-400 hover:text-gray-200"
      >
        <HiOutlineBars3 className="w-6 h-6" />
      </button>

      {/* 모바일 오버레이 */}
      {mobileOpen && (
        <div
          className="md:hidden fixed inset-0 bg-black/60 z-40"
          onClick={() => setMobileOpen(false)}
        />
      )}

      {/* 모바일 사이드바 (슬라이드) */}
      <aside
        className={`md:hidden fixed top-0 left-0 z-50 w-64 bg-gray-900 border-r border-gray-800 flex flex-col h-full transition-transform duration-200 ${
          mobileOpen ? "translate-x-0" : "-translate-x-full"
        }`}
      >
        {sidebarContent}
      </aside>

      {/* 데스크탑 사이드바 */}
      <aside className="hidden md:flex w-64 bg-gray-900 border-r border-gray-800 flex-col min-h-screen">
        {sidebarContent}
      </aside>
    </>
  );
}
