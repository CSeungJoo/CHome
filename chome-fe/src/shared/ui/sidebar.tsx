"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useAuthStore } from "@/stores/auth-store";
import {
  HiOutlineHome,
  HiOutlineCube,
  HiOutlineCpuChip,
  HiOutlineArrowRightOnRectangle,
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

  const handleLogout = () => {
    clearAuth();
    router.push("/login");
  };

  return (
    <aside className="w-64 bg-white border-r border-gray-200 flex flex-col min-h-screen">
      <div className="p-6">
        <h1 className="text-xl font-bold text-gray-900">CHome</h1>
        <p className="text-xs text-gray-500 mt-1">Smart Home Dashboard</p>
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
                  ? "bg-blue-50 text-blue-700"
                  : "text-gray-600 hover:bg-gray-100"
              }`}
            >
              <item.icon className="w-5 h-5" />
              {item.label}
            </Link>
          );
        })}
      </nav>

      <div className="p-3 border-t border-gray-200">
        <button
          onClick={handleLogout}
          className="flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium text-gray-600 hover:bg-gray-100 w-full transition-colors"
        >
          <HiOutlineArrowRightOnRectangle className="w-5 h-5" />
          로그아웃
        </button>
      </div>
    </aside>
  );
}
