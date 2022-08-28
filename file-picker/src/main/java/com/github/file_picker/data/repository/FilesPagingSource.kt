package com.github.file_picker.data.repository

import android.app.Application
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.file_picker.FileType
import com.github.file_picker.data.model.Media
import com.github.file_picker.extension.getStorageFiles

internal class FilesPagingSource(
    private val application: Application,
    private val fileType: FileType,
) : PagingSource<Int, Media>() {

    // the initial load size for the first page may be different from the requested size
    var initialLoadSize: Int = 0

    override fun getRefreshKey(state: PagingState<Int, Media>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Media> {
        return try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key ?: 1

            if (params.key == null) initialLoadSize = params.loadSize

            // work out the offset into the database to retrieve records from the page number,
            // allow for a different load size for the first page
            val offsetCalc = {
                if (nextPageNumber == 2)
                    initialLoadSize
                else
                    ((nextPageNumber - 1) * params.loadSize) + (initialLoadSize - params.loadSize)
            }

            val offset = offsetCalc.invoke()

            val files = application.getStorageFiles(
                fileType = fileType,
                limit = params.loadSize,
                offset = offset
            )
            val count = files.size

            LoadResult.Page(
                data = files,
                prevKey = null,
                nextKey = if (count < params.loadSize) null else nextPageNumber + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}