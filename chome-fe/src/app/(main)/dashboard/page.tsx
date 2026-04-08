"use client";

import { Card } from "@/shared/ui";
import { useHubs } from "@/features/hub/hooks";
import Link from "next/link";
import { HiOutlineCube, HiOutlineCpuChip, HiOutlineSignal } from "react-icons/hi2";

export default function DashboardPage() {
  const { data: hubsData, isLoading } = useHubs();

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-100 mb-6">대시보드</h1>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <Card>
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 bg-blue-600/20 rounded-lg flex items-center justify-center">
              <HiOutlineCube className="w-6 h-6 text-blue-400" />
            </div>
            <div>
              <p className="text-sm text-gray-500">등록된 허브</p>
              <p className="text-2xl font-bold text-gray-100">
                {isLoading ? "-" : hubsData?.totalCount ?? 0}
              </p>
            </div>
          </div>
        </Card>

        <Card>
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 bg-green-600/20 rounded-lg flex items-center justify-center">
              <HiOutlineCpuChip className="w-6 h-6 text-green-400" />
            </div>
            <div>
              <p className="text-sm text-gray-500">디바이스</p>
              <p className="text-2xl font-bold text-gray-100">-</p>
            </div>
          </div>
        </Card>

        <Card>
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 bg-purple-600/20 rounded-lg flex items-center justify-center">
              <HiOutlineSignal className="w-6 h-6 text-purple-400" />
            </div>
            <div>
              <p className="text-sm text-gray-500">SSE 연결</p>
              <p className="text-2xl font-bold text-gray-100">-</p>
            </div>
          </div>
        </Card>
      </div>

      <h2 className="text-lg font-semibold text-gray-100 mb-4">내 허브</h2>

      {isLoading ? (
        <div className="flex justify-center py-12">
          <div className="animate-spin h-8 w-8 border-4 border-blue-500 border-t-transparent rounded-full" />
        </div>
      ) : hubsData?.accessibleHubs.length === 0 ? (
        <Card>
          <div className="text-center py-8">
            <p className="text-gray-500 mb-4">등록된 허브가 없습니다</p>
            <Link
              href="/hubs"
              className="text-blue-400 hover:underline text-sm font-medium"
            >
              허브 등록하러 가기
            </Link>
          </div>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {hubsData?.accessibleHubs.map((hub) => (
            <Link key={hub.id} href={`/hubs/${hub.id}`}>
              <Card className="hover:border-blue-500/50 hover:shadow-md transition-all cursor-pointer">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="font-medium text-gray-100">{hub.alias}</p>
                    <p className="text-xs text-gray-500 mt-1">
                      S/N: {hub.serialNumber}
                    </p>
                  </div>
                  {hub.isOwner && (
                    <span className="text-xs bg-blue-600/20 text-blue-400 px-2 py-1 rounded-full">
                      소유자
                    </span>
                  )}
                </div>
              </Card>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
